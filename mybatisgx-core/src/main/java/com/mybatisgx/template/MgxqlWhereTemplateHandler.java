package com.mybatisgx.template;

import com.mybatisgx.annotation.LogicDelete;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.mgxql.model.expression.ConditionColumnExpression;
import com.mybatisgx.dsl.mgxql.model.expression.ConditionCompositeExpression;
import com.mybatisgx.dsl.mgxql.model.expression.SqlExpression;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.MethodInfo;
import com.mybatisgx.model.MethodParamInfo;
import com.mybatisgx.template.select.AliasContext;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL WHERE 条件模板处理器，直接从 WhereExpression 树 + BoundParam 渲染 MyBatis XML
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class MgxqlWhereTemplateHandler {

    private AliasContext aliasContext;

    public Element execute(EntityInfo entityInfo, MethodInfo methodInfo, WhereExpression rootExpression) {
        return this.execute(entityInfo, methodInfo, rootExpression, null);
    }

    public Element execute(EntityInfo entityInfo, MethodInfo methodInfo, WhereExpression rootExpression, AliasContext aliasContext) {
        this.aliasContext = aliasContext;
        if (rootExpression == null || ObjectUtils.isEmpty(rootExpression.getNodes())) {
            return null;
        }
        Element whereElement = DocumentHelper.createElement("where");
        this.renderExpression(whereElement, rootExpression, methodInfo.getDynamic());
        this.addOptimisticLockCondition(entityInfo, methodInfo, whereElement);
        this.addLogicDeleteCondition(entityInfo, whereElement);
        return whereElement;
    }

    private void addOptimisticLockCondition(EntityInfo entityInfo, MethodInfo methodInfo, Element whereElement) {
        if (entityInfo == null) {
            return;
        }
        ColumnInfo versionColumnInfo = entityInfo.getVersionColumnInfo();
        if (versionColumnInfo == null) {
            return;
        }
        if (methodInfo.getSqlCommandType() != SqlCommandType.UPDATE) {
            return;
        }
        MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
        List<String> argValueCommonPathItemList = new ArrayList<>();
        if (methodInfo.getBatch()) {
            argValueCommonPathItemList.add(entityParamInfo.getBatchItemName());
        }
        argValueCommonPathItemList.add(versionColumnInfo.getJavaColumnName());
        String valueExpression = StringUtils.join(argValueCommonPathItemList, ".");
        whereElement.addText(String.format(" and %s = #{%s}", versionColumnInfo.getDbColumnName(), valueExpression));
    }

    private void addLogicDeleteCondition(EntityInfo entityInfo, Element whereElement) {
        if (entityInfo == null) {
            return;
        }
        ColumnInfo logicDeleteColumnInfo = entityInfo.getLogicDeleteColumnInfo();
        if (logicDeleteColumnInfo == null) {
            return;
        }
        LogicDelete logicDelete = logicDeleteColumnInfo.getLogicDelete();
        whereElement.addText(String.format(" and %s = '%s'", logicDeleteColumnInfo.getDbColumnName(), logicDelete.show()));
    }

    private void renderExpression(Element parentElement, WhereExpression expression, Boolean dynamic) {
        List<WhereConditionNode> nodes = expression.getNodes();
        if (ObjectUtils.isEmpty(nodes)) {
            return;
        }
        for (WhereConditionNode node : nodes) {
            if (node.isNested()) {
                this.renderNestedNode(parentElement, node, dynamic);
            } else {
                this.renderLeafNode(parentElement, node, dynamic);
            }
        }
    }

    private void renderNestedNode(Element parentElement, WhereConditionNode node, Boolean dynamic) {
        WhereExpression subExpression = node.getSubExpression();
        LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null ? logicOp.getValue() : "";
        parentElement.addText(String.format(" %s (", logicStr));
        this.renderExpression(parentElement, subExpression, dynamic);
        parentElement.addText(")");
    }

    private void renderLeafNode(Element parentElement, WhereConditionNode node, Boolean dynamic) {
        BoundParam boundParam = node.getBoundParam();
        if (boundParam == null) {
            return;
        }

        ColumnInfo columnInfo = node.getColumnInfo();
        if (columnInfo != null && columnInfo.getLogicDelete() != null) {
            return;
        }

        Element targetElement = this.wrapOptionalIf(parentElement, node, dynamic);

        ParamKind kind = boundParam.getKind();
        if (kind == ParamKind.NULL_TYPE) {
            this.renderNullCondition(targetElement, node, boundParam);
            return;
        }

        ComparisonOperator operator = boundParam.getOperator();
        if (operator == ComparisonOperator.IN) {
            this.renderInCondition(targetElement, node, boundParam);
            return;
        }
        if (operator == ComparisonOperator.BETWEEN) {
            this.renderBetweenCondition(targetElement, node, boundParam);
            return;
        }
        if (operator == ComparisonOperator.LIKE || operator == ComparisonOperator.STARTING_WITH || operator == ComparisonOperator.ENDING_WITH) {
            this.renderLikeCondition(targetElement, node, boundParam);
            return;
        }

        this.renderCommonCondition(targetElement, node, boundParam);
    }

    private Element wrapOptionalIf(Element parentElement, WhereConditionNode node, Boolean dynamic) {
        if (node.isOptional() || dynamic) {
            BoundParam boundParam = node.getBoundParam();
            if (boundParam != null && boundParam.getOperator() != null && boundParam.getOperator().isNullComparisonOperator()) {
                return parentElement;
            }
            if (boundParam != null && ObjectUtils.isNotEmpty(boundParam.getEntries())) {
                List<String> paramPath = boundParam.getEntries().get(0).getParamPath();
                if (ObjectUtils.isNotEmpty(paramPath)) {
                    String testExpression = MybatisXmlHelper.getTestExpression(paramPath);
                    return MybatisXmlHelper.buildIfElement(parentElement, testExpression);
                }
            }
        }
        return parentElement;
    }

    private void renderNullCondition(Element parentElement, WhereConditionNode node, BoundParam boundParam) {
        LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null ? logicOp.getValue() : "";
        ComparisonOperator operator = boundParam.getOperator();
        String columnSql = this.getColumnSql(boundParam);
        parentElement.addText(String.format(" %s %s %s", logicStr, columnSql, operator.getValue()));
    }

    private void renderCommonCondition(Element parentElement, WhereConditionNode node, BoundParam boundParam) {
        List<BoundParamEntry> entries = boundParam.getEntries();
        for (BoundParamEntry entry : entries) {
            LogicOperator logicOp = entry.getLogicOperator();
            if (logicOp == null || logicOp == LogicOperator.NULL) {
                logicOp = node.getLogicOperator();
            }
            String logicStr = logicOp != null ? logicOp.getValue() : "";
            String columnSql = this.resolveColumnSql(entry.getSqlExpression());
            String operatorStr = boundParam.getOperator().getValue();
            String notStr = boundParam.getNotOperator() != null ? " " + boundParam.getNotOperator().getValue() : "";
            String paramExpr = this.buildParamExpression(entry);
            parentElement.addText(String.format(" %s %s%s %s %s", logicStr, columnSql, notStr, operatorStr, paramExpr));
        }
    }

    private void renderInCondition(Element parentElement, WhereConditionNode node, BoundParam boundParam) {
        List<BoundParamEntry> entries = boundParam.getEntries();
        if (ObjectUtils.isEmpty(entries)) {
            return;
        }
        BoundParamEntry entry = entries.get(0);
        com.mybatisgx.dsl.mgxql.model.LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null ? logicOp.getValue() : "";
        String columnSql = this.resolveColumnSql(entry.getSqlExpression());
        String notStr = boundParam.getNotOperator() != null ? " " + boundParam.getNotOperator().getValue() : "";
        String collectionPath = StringUtils.join(entry.getParamPath(), ".");
        parentElement.addText(String.format(" %s %s%s in", logicStr, columnSql, notStr));
        Element foreachElement = MybatisXmlHelper.buildForeachElement(collectionPath);
        parentElement.add(foreachElement);
    }

    private void renderBetweenCondition(Element parentElement, WhereConditionNode node, BoundParam boundParam) {
        List<BoundParamEntry> entries = boundParam.getEntries();
        if (ObjectUtils.isEmpty(entries)) {
            return;
        }
        BoundParamEntry entry = entries.get(0);
        LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null ? logicOp.getValue() : "";
        String columnSql = this.resolveColumnSql(entry.getSqlExpression());
        String notStr = boundParam.getNotOperator() != null ? " " + boundParam.getNotOperator().getValue() : "";
        String path = StringUtils.join(entry.getParamPath(), ".");
        parentElement.addText(String.format(" %s %s%s between #{%s[0]} and #{%s[1]}", logicStr, columnSql, notStr, path, path));
    }

    private void renderLikeCondition(Element parentElement, WhereConditionNode node, BoundParam boundParam) {
        List<BoundParamEntry> entries = boundParam.getEntries();
        if (ObjectUtils.isEmpty(entries)) {
            return;
        }
        BoundParamEntry entry = entries.get(0);
        LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null ? logicOp.getValue() : "";
        String columnSql = this.resolveColumnSql(entry.getSqlExpression());
        String notStr = boundParam.getNotOperator() != null ? " " + boundParam.getNotOperator().getValue() : "";

        String bindKey = "_like_" + StringUtils.join(entry.getParamPath(), "_");
        String bindValuePath = StringUtils.join(entry.getParamPath(), ".");
        String likeExpression = this.buildLikeExpression(boundParam.getOperator(), bindValuePath);
        Element bindElement = MybatisXmlHelper.buildBindElement(bindKey, likeExpression);
        parentElement.add(bindElement);
        parentElement.addText(String.format(" %s %s%s like #{%s}", logicStr, columnSql, notStr, bindKey));
    }

    private String buildLikeExpression(ComparisonOperator operator, String bindValuePath) {
        if (operator == ComparisonOperator.STARTING_WITH) {
            return "'%'+" + bindValuePath;
        }
        if (operator == ComparisonOperator.ENDING_WITH) {
            return bindValuePath + "+'%'";
        }
        return "'%'+" + bindValuePath + "+'%'";
    }

    private String buildParamExpression(BoundParamEntry entry) {
        if (ObjectUtils.isNotEmpty(entry.getParamPath())) {
            String path = StringUtils.join(entry.getParamPath(), ".");
            String typeHandlerStr = "";
            if (StringUtils.isNotBlank(entry.getTypeHandler())) {
                typeHandlerStr = String.format(", typeHandler=%s", entry.getTypeHandler());
            }
            return String.format("#{%s%s}", path, typeHandlerStr);
        }
        if (entry.getLiteralValue() != null) {
            return entry.getLiteralValue().toString();
        }
        return "";
    }

    private String getColumnSql(BoundParam boundParam) {
        if (ObjectUtils.isNotEmpty(boundParam.getEntries())) {
            BoundParamEntry entry = boundParam.getEntries().get(0);
            return this.resolveColumnSql(entry.getSqlExpression());
        }
        return "";
    }

    /**
     * 解析 SQL 表达式中的 MGXQL 别名为 SQL 表别名。
     * <p>
     * aliasContext 为 null 时（UPDATE/DELETE 场景）直接调用 toSql()；
     * ConditionColumnExpression 时通过 resolveTableAlias 解析 tableAlias；
     * ConditionCompositeExpression 时遍历子列逐个解析；其他类型兜底调用 toSql()。
     */
    private String resolveColumnSql(SqlExpression expr) {
        if (expr == null) {
            return "";
        }
        if (this.aliasContext == null) {
            return expr.toSql();
        }
        if (expr instanceof ConditionColumnExpression) {
            ConditionColumnExpression col = (ConditionColumnExpression) expr;
            String mgxqlAlias = col.getTableAlias();
            if (StringUtils.isNotBlank(mgxqlAlias)) {
                String dbAlias = this.aliasContext.resolveTableAlias(mgxqlAlias);
                return dbAlias + "." + col.getDbColumnName();
            }
            return col.getDbColumnName();
        }
        if (expr instanceof ConditionCompositeExpression) {
            ConditionCompositeExpression composite = (ConditionCompositeExpression) expr;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < composite.getColumns().size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                ConditionColumnExpression col = composite.getColumns().get(i);
                String mgxqlAlias = col.getTableAlias();
                if (StringUtils.isNotBlank(mgxqlAlias)) {
                    String dbAlias = this.aliasContext.resolveTableAlias(mgxqlAlias);
                    sb.append(dbAlias).append(".").append(col.getDbColumnName());
                } else {
                    sb.append(col.getDbColumnName());
                }
            }
            return sb.toString();
        }
        return expr.toSql();
    }
}
