package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;

/**
 * 字段存在性校验器
 * <p>
 * 校验SELECT字段、WHERE条件字段、ORDER BY字段、GROUP BY字段是否存在于对应实体中
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class FieldChecker implements MgxqlChecker {

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void check(MgxqlStatement statement, CheckerContext context) {
        // 校验SELECT字段
        if (statement.getSelectItems() != null) {
            for (SelectItem selectItem : statement.getSelectItems()) {
                if (selectItem.getType() == SelectItemType.COLUMN && selectItem.getFieldName() != null) {
                    this.checkFieldExistence(selectItem.getEntityAlias(), selectItem.getFieldName(), "SELECT", context);
                }
                // 聚合函数参数字段校验（COUNT的"*"和"1"是约定值，不是真实字段引用，跳过校验）
                if (selectItem.getAggregateFieldRef() != null
                        && !(selectItem.getType() == SelectItemType.COUNT
                        && isCountConventionValue(selectItem.getAggregateFieldRef().getFieldName()))) {
                    FieldReference fieldRef = selectItem.getAggregateFieldRef();
                    this.checkFieldExistence(fieldRef.getEntityAlias(), fieldRef.getFieldName(), "SELECT", context);
                }
            }
        }

        // 校验WHERE条件字段
        if (statement.getWhereClause() != null && statement.getWhereClause().getRootExpression() != null) {
            this.checkConditionExpressionFields(statement.getWhereClause().getRootExpression(), context);
        }

        // 校验ORDER BY字段
        if (statement.getOrderByClause() != null) {
            for (OrderByItem item : statement.getOrderByClause().getItems()) {
                if (item.getField() != null) {
                    this.checkFieldExistence(item.getField().getEntityAlias(), item.getField().getFieldName(), "ORDER BY", context);
                }
            }
        }

        // 校验GROUP BY字段
        if (statement.getGroupByClause() != null) {
            for (FieldReference fieldRef : statement.getGroupByClause().getFields()) {
                this.checkFieldExistence(fieldRef.getEntityAlias(), fieldRef.getFieldName(), "GROUP BY", context);
            }
        }

        // 校验HAVING聚合函数参数字段
        if (statement.getHavingClause() != null && statement.getHavingClause().getConditions() != null) {
            for (HavingCondition condition : statement.getHavingClause().getConditions()) {
                if (condition.getAggregateFunction() != null && condition.getAggregateFunction().getAggregateFieldRef() != null) {
                    SelectItem aggItem = condition.getAggregateFunction();
                    if (aggItem.getType() == SelectItemType.COUNT
                            && isCountConventionValue(aggItem.getAggregateFieldRef().getFieldName())) {
                        continue;
                    }
                    FieldReference fieldRef = aggItem.getAggregateFieldRef();
                    this.checkFieldExistence(fieldRef.getEntityAlias(), fieldRef.getFieldName(), "HAVING", context);
                }
            }
        }
    }

    /**
     * 递归校验条件表达式中的字段
     */
    private void checkConditionExpressionFields(ConditionExpression expression, CheckerContext context) {
        if (expression == null || expression.getNodes() == null) {
            return;
        }
        for (ConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                // 递归检查嵌套表达式
                this.checkConditionExpressionFields(node.getSubExpression(), context);
            } else if (node.getFieldName() != null) {
                this.checkFieldExistence(node.getFieldAlias(), node.getFieldName(), "WHERE", context);
            }
        }
    }

    private static boolean isCountConventionValue(String fieldName) {
        return "*".equals(fieldName) || "1".equals(fieldName);
    }

    /**
     * 校验字段是否存在于对应实体中
     */
    private void checkFieldExistence(String entityAlias, String fieldName, String clauseName, CheckerContext context) {
        EntityInfo entityInfo;
        if (entityAlias != null && !entityAlias.isEmpty()) {
            entityInfo = context.getEntityInfoByAlias(entityAlias);
            if (entityInfo == null) {
                // 别名未找到实体，跳过（由EntityChecker报告错误）
                return;
            }
        } else {
            // 没有别名时使用主实体
            entityInfo = context.getPrimaryEntityInfo();
            if (entityInfo == null) {
                return;
            }
        }

        ColumnInfo columnInfo = entityInfo.getColumnInfo(fieldName);
        if (columnInfo == null) {
            context.addError(String.format("%s 子句中字段 '%s' 在实体 '%s' 中不存在",
                    clauseName, fieldName, entityInfo.getClazz().getSimpleName()));
        }
    }
}
