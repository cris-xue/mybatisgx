package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.mgxql.model.expression.HavingAggregateExpression;
import com.mybatisgx.dsl.mgxql.model.expression.HavingSqlExpression;
import com.mybatisgx.dsl.mgxql.model.expression.SqlExpression;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * HAVING 条件模板处理器，从 HavingExpression 树渲染 MyBatis XML
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class HavingTemplateHandler {

    private AliasContext aliasContext;

    public String execute(HavingExpression expression) {
        return this.execute(expression, null);
    }

    public String execute(HavingExpression expression, AliasContext aliasContext) {
        this.aliasContext = aliasContext;
        if (expression == null || ObjectUtils.isEmpty(expression.getNodes())) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" having");
        this.renderExpression(sb, expression);
        return sb.toString();
    }

    private void renderExpression(StringBuilder sb, HavingExpression expression) {
        for (HavingConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                this.renderNestedNode(sb, node);
            } else {
                this.renderLeafNode(sb, node);
            }
        }
    }

    private void renderNestedNode(StringBuilder sb, HavingConditionNode node) {
        LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null && logicOp != LogicOperator.NULL ? " " + logicOp.getValue() : "";
        sb.append(logicStr).append(" (");
        this.renderExpression(sb, node.getSubExpression());
        sb.append(")");
    }

    private void renderLeafNode(StringBuilder sb, HavingConditionNode node) {
        BoundParam boundParam = node.getBoundParam();
        if (boundParam == null) {
            return;
        }

        LogicOperator logicOp = node.getLogicOperator();
        String logicStr = logicOp != null && logicOp != LogicOperator.NULL ? " " + logicOp.getValue() : "";

        String leftSql = "";
        if (ObjectUtils.isNotEmpty(boundParam.getEntries())) {
            BoundParamEntry entry = boundParam.getEntries().get(0);
            if (entry.getSqlExpression() != null) {
                leftSql = this.resolveColumnSql(entry.getSqlExpression());
            }
        }

        String operatorStr = boundParam.getOperator().getValue();

        if (ObjectUtils.isNotEmpty(boundParam.getEntries())) {
            BoundParamEntry entry = boundParam.getEntries().get(0);
            if (entry.getLiteralValue() != null) {
                sb.append(String.format("%s %s %s %s", logicStr, leftSql, operatorStr, entry.getLiteralValue()));
            } else if (ObjectUtils.isNotEmpty(entry.getParamPath())) {
                String paramPath = StringUtils.join(entry.getParamPath(), ".");
                sb.append(String.format("%s %s %s #{%s}", logicStr, leftSql, operatorStr, paramPath));
            } else {
                sb.append(String.format("%s %s %s", logicStr, leftSql, operatorStr));
            }
        }
    }

    /**
     * 解析 SQL 表达式中的 MGXQL 别名为 SQL 表别名。
     * <p>
     * 对于 HavingAggregateExpression，解析 argument 中的 entityAlias 部分；
     * aliasContext 为 null 时直接调用 toSql()。
     */
    private String resolveColumnSql(SqlExpression expr) {
        if (expr == null) {
            return "";
        }
        if (this.aliasContext == null) {
            return expr.toSql();
        }
        if (expr instanceof HavingAggregateExpression) {
            return this.resolveAggregateExpression((HavingAggregateExpression) expr);
        }
        return expr.toSql();
    }

    /**
     * 解析聚合函数表达式中的 MGXQL 别名。
     * <p>
     * 将 argument 中的 "u.id" 解析为 "t0.id"（t0 为 SQL 表别名），
     * 然后重新构造 "count(t0.id)" 形式的 SQL。
     */
    private String resolveAggregateExpression(HavingAggregateExpression expr) {
        String argument = expr.getArgument();
        if (StringUtils.isNotBlank(argument) && argument.contains(".")) {
            int dotIndex = argument.indexOf('.');
            String mgxqlAlias = argument.substring(0, dotIndex);
            String columnName = argument.substring(dotIndex + 1);
            String dbAlias = this.aliasContext.resolveTableAlias(mgxqlAlias);
            String resolvedArgument = dbAlias + "." + columnName;
            return expr.getFunction().getSqlKeyword() + "(" + resolvedArgument + ")";
        }
        return expr.toSql();
    }
}
