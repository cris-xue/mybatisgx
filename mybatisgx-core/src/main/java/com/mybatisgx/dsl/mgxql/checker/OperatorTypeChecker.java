package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.ComparisonOperator;
import com.mybatisgx.dsl.mgxql.model.WhereExpression;
import com.mybatisgx.dsl.mgxql.model.WhereConditionNode;
import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;

/**
 * 运算符类型匹配校验器
 * <p>
 * 校验比较运算符与字段类型是否兼容（如 LIKE 只能用于字符串类型）
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class OperatorTypeChecker implements MgxqlSemanticChecker {

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public boolean support(MgxqlStatement mgxqlStatement) {
        return true;
    }

    @Override
    public void check(MgxqlStatement statement, CheckerContext context) {
        if (statement.getWhereClause() == null || statement.getWhereClause().getRootExpression() == null) {
            return;
        }
        this.checkConditionExpression(statement.getWhereClause().getRootExpression(), context);
    }

    private void checkConditionExpression(WhereExpression expression, CheckerContext context) {
        if (expression == null || expression.getNodes() == null) {
            return;
        }
        for (WhereConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                this.checkConditionExpression(node.getSubExpression(), context);
            } else if (node.getFieldName() != null && node.getOperator() != null) {
                this.checkOperatorTypeCompatibility(node, context);
            }
        }
    }

    /**
     * 校验运算符与字段类型的兼容性
     */
    private void checkOperatorTypeCompatibility(WhereConditionNode node, CheckerContext context) {
        EntityInfo entityInfo = this.resolveEntityInfo(node.getFieldAlias(), context);
        if (entityInfo == null) {
            return;
        }

        ColumnInfo columnInfo = entityInfo.getColumnInfo(node.getFieldName());
        if (columnInfo == null || columnInfo.getJavaType() == null) {
            return;
        }

        ComparisonOperator operator = node.getOperator();
        Class<?> fieldType = columnInfo.getJavaType();

        // LIKE类运算符只能用于字符串类型
        if (isLikeOperator(operator) && !String.class.isAssignableFrom(fieldType)) {
            context.addError(String.format(
                    "字段 '%s' 的类型为 %s，不支持 LIKE 类运算符，LIKE 仅适用于 String 类型",
                    node.getFieldName(), fieldType.getSimpleName()));
        }

        // BETWEEN运算符需要可比较类型
        if (operator == ComparisonOperator.BETWEEN && !isComparableType(fieldType)) {
            context.addError(String.format(
                    "字段 '%s' 的类型为 %s，不支持 BETWEEN 运算符，BETWEEN 仅适用于可比较类型（数值、日期等）",
                    node.getFieldName(), fieldType.getSimpleName()));
        }

        // IN运算符不适用于布尔类型
        if (operator == ComparisonOperator.IN && (fieldType == Boolean.class || fieldType == boolean.class)) {
            context.addError(String.format(
                    "字段 '%s' 的类型为 Boolean，不支持 IN 运算符",
                    node.getFieldName()));
        }
    }

    private EntityInfo resolveEntityInfo(String alias, CheckerContext context) {
        if (alias != null && !alias.isEmpty()) {
            return context.getEntityInfoByAlias(alias);
        }
        return context.getPrimaryEntityInfo();
    }

    private boolean isLikeOperator(ComparisonOperator operator) {
        return operator == ComparisonOperator.LIKE
                || operator == ComparisonOperator.STARTING_WITH
                || operator == ComparisonOperator.ENDING_WITH;
    }

    private boolean isComparableType(Class<?> type) {
        return Number.class.isAssignableFrom(type)
                || type == int.class || type == long.class || type == double.class || type == float.class
                || type == short.class || type == byte.class
                || java.util.Date.class.isAssignableFrom(type)
                || java.time.temporal.Temporal.class.isAssignableFrom(type)
                || String.class.isAssignableFrom(type);
    }
}
