package com.mybatisgx.template;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.mgxql.model.expression.SqlExpression;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.MethodInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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

    public Element execute(MethodInfo methodInfo, WhereExpression rootExpression) {
        if (rootExpression == null || ObjectUtils.isEmpty(rootExpression.getNodes())) {
            return null;
        }
        Element whereElement = DocumentHelper.createElement("where");
        this.renderExpression(whereElement, rootExpression, methodInfo.getDynamic());
        return whereElement;
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
        com.mybatisgx.dsl.mgxql.model.LogicOperator logicOp = node.getLogicOperator();
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

        com.mybatisgx.dsl.mgxql.model.ComparisonOperator operator = boundParam.getOperator();
        if (operator == com.mybatisgx.dsl.mgxql.model.ComparisonOperator.IN) {
            this.renderInCondition(targetElement, node, boundParam);
            return;
        }
        if (operator == com.mybatisgx.dsl.mgxql.model.ComparisonOperator.BETWEEN) {
            this.renderBetweenCondition(targetElement, node, boundParam);
            return;
        }
        if (operator == com.mybatisgx.dsl.mgxql.model.ComparisonOperator.LIKE
                || operator == com.mybatisgx.dsl.mgxql.model.ComparisonOperator.STARTING_WITH
                || operator == com.mybatisgx.dsl.mgxql.model.ComparisonOperator.ENDING_WITH) {
            this.renderLikeCondition(targetElement, node, boundParam);
            return;
        }

        this.renderCommonCondition(targetElement, node, boundParam);
    }

    private Element wrapOptionalIf(Element parentElement, WhereConditionNode node, Boolean dynamic) {
        if (node.isOptional() && dynamic) {
            BoundParam boundParam = node.getBoundParam();
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
        com.mybatisgx.dsl.mgxql.model.LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null ? logicOp.getValue() : "";
        com.mybatisgx.dsl.mgxql.model.ComparisonOperator operator = boundParam.getOperator();
        String columnSql = this.getColumnSql(boundParam);
        parentElement.addText(String.format(" %s %s %s", logicStr, columnSql, operator.getValue()));
    }

    private void renderCommonCondition(Element parentElement, WhereConditionNode node, BoundParam boundParam) {
        List<BoundParamEntry> entries = boundParam.getEntries();
        for (BoundParamEntry entry : entries) {
            com.mybatisgx.dsl.mgxql.model.LogicOperator logicOp = entry.getLogicOperator();
            if (logicOp == null || logicOp == com.mybatisgx.dsl.mgxql.model.LogicOperator.NULL) {
                logicOp = node.getLogicOperator();
            }
            String logicStr = logicOp != null ? logicOp.getValue() : "";
            String columnSql = entry.getSqlExpression() != null ? entry.getSqlExpression().toSql() : "";
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
        String columnSql = entry.getSqlExpression() != null ? entry.getSqlExpression().toSql() : "";
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
        com.mybatisgx.dsl.mgxql.model.LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null ? logicOp.getValue() : "";
        String columnSql = entry.getSqlExpression() != null ? entry.getSqlExpression().toSql() : "";
        String path = StringUtils.join(entry.getParamPath(), ".");
        parentElement.addText(String.format(" %s %s between #{%s[0]} and #{%s[1]}", logicStr, columnSql, path, path));
    }

    private void renderLikeCondition(Element parentElement, WhereConditionNode node, BoundParam boundParam) {
        List<BoundParamEntry> entries = boundParam.getEntries();
        if (ObjectUtils.isEmpty(entries)) {
            return;
        }
        BoundParamEntry entry = entries.get(0);
        com.mybatisgx.dsl.mgxql.model.LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null ? logicOp.getValue() : "";
        String columnSql = entry.getSqlExpression() != null ? entry.getSqlExpression().toSql() : "";
        String notStr = boundParam.getNotOperator() != null ? " " + boundParam.getNotOperator().getValue() : "";

        String bindKey = StringUtils.join(entry.getParamPath(), "_");
        String bindValuePath = StringUtils.join(entry.getParamPath(), ".");
        String likeExpression = this.buildLikeExpression(boundParam.getOperator(), bindValuePath);
        Element bindElement = MybatisXmlHelper.buildBindElement(bindKey, likeExpression);
        parentElement.add(bindElement);
        parentElement.addText(String.format(" %s %s%s like #{%s}", logicStr, columnSql, notStr, bindKey));
    }

    private String buildLikeExpression(com.mybatisgx.dsl.mgxql.model.ComparisonOperator operator, String bindValuePath) {
        if (operator == com.mybatisgx.dsl.mgxql.model.ComparisonOperator.STARTING_WITH) {
            return "'%'+" + bindValuePath;
        }
        if (operator == com.mybatisgx.dsl.mgxql.model.ComparisonOperator.ENDING_WITH) {
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
            if (entry.getSqlExpression() != null) {
                return entry.getSqlExpression().toSql();
            }
        }
        return "";
    }
}
