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
    public void check(MgxqlStatement statement, SyntaxCheckerContext context) {
        boolean isDeleteOrUpdate = statement.getCommandType() == SqlCommandType.DELETE
                || statement.getCommandType() == SqlCommandType.UPDATE;
        boolean hasMultipleEntities = context.isHasMultipleEntities();

        // 校验SELECT字段
        if (statement.getSelectItems() != null) {
            for (SelectItem selectItem : statement.getSelectItems()) {
                // 普通字段列
                if (selectItem.getType() == SelectItemType.COLUMN) {
                    checkFieldAlias(selectItem.getEntityAlias(), selectItem.getFieldName(),
                            "SELECT", hasMultipleEntities, isDeleteOrUpdate, context);
                }
                // 聚合函数参数字段
                if (selectItem.getAggregateFieldRef() != null) {
                    FieldReference fieldRef = selectItem.getAggregateFieldRef();
                    checkFieldAlias(fieldRef.getEntityAlias(), fieldRef.getFieldName(),
                            "SELECT", hasMultipleEntities, isDeleteOrUpdate, context);
                }
            }
        }

        // 校验WHERE条件字段
        if (statement.getWhereClause() != null && statement.getWhereClause().getRootExpression() != null) {
            checkConditionExpressionFields(statement.getWhereClause().getRootExpression(),
                    hasMultipleEntities, isDeleteOrUpdate, context);
        }

        // 校验ORDER BY字段
        if (statement.getOrderByClause() != null) {
            for (OrderByItem item : statement.getOrderByClause().getItems()) {
                if (item.getField() != null) {
                    checkFieldAlias(item.getField().getEntityAlias(), item.getField().getFieldName(),
                            "ORDER BY", hasMultipleEntities, isDeleteOrUpdate, context);
                }
            }
        }

        // 校验GROUP BY字段
        if (statement.getGroupByClause() != null) {
            for (FieldReference fieldRef : statement.getGroupByClause().getFields()) {
                checkFieldAlias(fieldRef.getEntityAlias(), fieldRef.getFieldName(),
                        "GROUP BY", hasMultipleEntities, isDeleteOrUpdate, context);
            }
        }

        // 校验HAVING聚合函数参数字段
        if (statement.getHavingClause() != null && statement.getHavingClause().getConditions() != null) {
            for (HavingCondition condition : statement.getHavingClause().getConditions()) {
                if (condition.getAggregateFunction() != null && condition.getAggregateFunction().getAggregateFieldRef() != null) {
                    FieldReference fieldRef = condition.getAggregateFunction().getAggregateFieldRef();
                    checkFieldAlias(fieldRef.getEntityAlias(), fieldRef.getFieldName(),
                            "HAVING", hasMultipleEntities, isDeleteOrUpdate, context);
                }
            }
        }
    }

    private void checkConditionExpressionFields(ConditionExpression expression,
                                                 boolean hasMultipleEntities, boolean isDeleteOrUpdate,
                                                 SyntaxCheckerContext context) {
        if (expression == null || expression.getNodes() == null) {
            return;
        }
        for (ConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                checkConditionExpressionFields(node.getSubExpression(), hasMultipleEntities, isDeleteOrUpdate, context);
            } else if (node.getFieldName() != null) {
                checkFieldAlias(node.getFieldAlias(), node.getFieldName(),
                        "WHERE", hasMultipleEntities, isDeleteOrUpdate, context);
            }
        }
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
