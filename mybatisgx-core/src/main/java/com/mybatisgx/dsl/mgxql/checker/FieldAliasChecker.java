package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * 字段别名校验器
 * <p>
 * R3: 多实体时字段引用必须带别名前缀（禁止裸字段）
 * R4: 任何场景，引用的别名必须存在于FROM定义
 * R5: DELETE/UPDATE中不允许使用别名前缀
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class FieldAliasChecker implements MgxqlSyntaxChecker {

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean support(MgxqlStatement mgxqlStatement) {
        return mgxqlStatement instanceof SelectStatement;
    }

    @Override
    public void check(MgxqlStatement mgxqlStatement, SyntaxCheckerContext context) {
        SelectStatement selectStatement = (SelectStatement) mgxqlStatement;

        boolean isDeleteOrUpdate = selectStatement.getCommandType() == SqlCommandType.DELETE
                || selectStatement.getCommandType() == SqlCommandType.UPDATE;
        boolean hasMultipleEntities = context.isHasMultipleEntities();

        // 校验SELECT字段
        if (selectStatement.getSelectItems() != null) {
            for (SelectItem selectItem : selectStatement.getSelectItems()) {
                // 普通字段列
                if (selectItem.getType() == SelectItemType.COLUMN) {
                    checkFieldAlias(selectItem.getEntityAlias(), selectItem.getFieldName(),
                            "SELECT", hasMultipleEntities, isDeleteOrUpdate, context);
                }
                // 聚合函数参数字段（COUNT的"*"和"1"是约定值，不是真实字段引用，跳过校验）
                if (selectItem.getAggregateFieldRef() != null
                        && !(selectItem.getType() == SelectItemType.COUNT
                        && isCountConventionValue(selectItem.getAggregateFieldRef().getFieldName()))) {
                    FieldReference fieldRef = selectItem.getAggregateFieldRef();
                    checkFieldAlias(fieldRef.getEntityAlias(), fieldRef.getFieldName(),
                            "SELECT", hasMultipleEntities, isDeleteOrUpdate, context);
                }
            }
        }

        // 校验WHERE条件字段
        if (selectStatement.getWhereClause() != null && selectStatement.getWhereClause().getRootExpression() != null) {
            checkConditionExpressionFields(selectStatement.getWhereClause().getRootExpression(),
                    hasMultipleEntities, isDeleteOrUpdate, context);
        }

        // 校验ORDER BY字段
        if (selectStatement.getOrderByClause() != null) {
            for (OrderByItem item : selectStatement.getOrderByClause().getItems()) {
                if (item.getField() != null) {
                    checkFieldAlias(item.getField().getEntityAlias(), item.getField().getFieldName(),
                            "ORDER BY", hasMultipleEntities, isDeleteOrUpdate, context);
                }
            }
        }

        // 校验GROUP BY字段
        if (selectStatement.getGroupByClause() != null) {
            for (FieldReference fieldRef : selectStatement.getGroupByClause().getFields()) {
                checkFieldAlias(fieldRef.getEntityAlias(), fieldRef.getFieldName(),
                        "GROUP BY", hasMultipleEntities, isDeleteOrUpdate, context);
            }
        }

        // 校验HAVING聚合函数参数字段
        if (selectStatement.getHavingClause() != null && selectStatement.getHavingClause().getConditions() != null) {
            for (HavingCondition condition : selectStatement.getHavingClause().getConditions()) {
                if (condition.getAggregateFunction() != null && condition.getAggregateFunction().getAggregateFieldRef() != null) {
                    SelectItem aggItem = condition.getAggregateFunction();
                    if (aggItem.getType() == SelectItemType.COUNT
                            && isCountConventionValue(aggItem.getAggregateFieldRef().getFieldName())) {
                        continue;
                    }
                    FieldReference fieldRef = aggItem.getAggregateFieldRef();
                    checkFieldAlias(fieldRef.getEntityAlias(), fieldRef.getFieldName(),
                            "HAVING", hasMultipleEntities, isDeleteOrUpdate, context);
                }
            }
        }
    }

    private void checkConditionExpressionFields(WhereExpression expression,
                                                boolean hasMultipleEntities, boolean isDeleteOrUpdate,
                                                SyntaxCheckerContext context) {
        if (expression == null || expression.getNodes() == null) {
            return;
        }
        for (WhereConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                checkConditionExpressionFields(node.getSubExpression(), hasMultipleEntities, isDeleteOrUpdate, context);
            } else if (node.getFieldName() != null) {
                checkFieldAlias(node.getFieldAlias(), node.getFieldName(),
                        "WHERE", hasMultipleEntities, isDeleteOrUpdate, context);
            }
        }
    }

    private static boolean isCountConventionValue(String fieldName) {
        return "*".equals(fieldName) || "1".equals(fieldName);
    }

    private void checkFieldAlias(String entityAlias, String fieldName, String clauseName,
                                 boolean hasMultipleEntities, boolean isDeleteOrUpdate,
                                 SyntaxCheckerContext context) {
        // R5: DELETE/UPDATE中不允许使用别名前缀
        if (isDeleteOrUpdate && entityAlias != null && !entityAlias.isEmpty()) {
            context.addError(String.format("DELETE/UPDATE语句中不允许使用实体别名前缀 '%s'", entityAlias));
            return;
        }

        if (entityAlias != null && !entityAlias.isEmpty()) {
            // R4: 引用的别名必须存在于FROM定义
            if (!context.isAliasDefined(entityAlias)) {
                context.addError(String.format("%s子句中别名 '%s' 未在FROM子句中定义", clauseName, entityAlias));
            }
        } else {
            // R3: 多实体时裸字段不允许
            if (hasMultipleEntities) {
                context.addError(String.format("%s子句中字段 '%s' 缺少实体别名前缀", clauseName, fieldName));
            }
        }
    }
}
