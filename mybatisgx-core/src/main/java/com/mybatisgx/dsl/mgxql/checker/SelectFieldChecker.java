package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;

/**
 * 字段存在性校验器
 * <p>
 * 校验SELECT字段、WHERE条件字段、ORDER BY字段、GROUP BY字段是否存在于对应实体中
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class SelectFieldChecker extends FieldChecker {

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean support(MgxqlStatement mgxqlStatement) {
        return mgxqlStatement instanceof SelectStatement;
    }

    @Override
    public void check(MgxqlStatement mgxqlStatement, CheckerContext context) {
        SelectStatement selectStatement = (SelectStatement) mgxqlStatement;

        // 校验规则：聚合与普通字段不可共存
        this.checkAggregateCoexistence(selectStatement, context);

        // 校验并绑定 SELECT 字段
        if (selectStatement.getSelectItems() != null) {
            for (SelectItem selectItem : selectStatement.getSelectItems()) {
                FieldReference fieldRef = selectItem.getFieldRef();
                if (fieldRef == null) {
                    continue;
                }
                // COLUMN_ALL 不绑定 ColumnInfo（通过 entityAlias 关联 FromEntity.entityInfo 展开全部列）
                if (selectItem.getType() == SelectItemType.COLUMN_ALL) {
                    continue;
                }
                // 聚合参数为 ASTERISK/NUMBER 时非真实字段，跳过字段校验与绑定
                if (isAggregateConventionValue(selectItem.getArgumentKind())) {
                    continue;
                }
                this.resolveAndSetFieldReferenceColumnInfo(fieldRef, "SELECT", context);
            }
        }

        // 校验并绑定ORDER BY字段
        if (selectStatement.getOrderByClause() != null) {
            for (OrderByItem item : selectStatement.getOrderByClause().getItems()) {
                if (item.getField() != null) {
                    this.resolveAndSetFieldReferenceColumnInfo(item.getField(), "ORDER BY", context);
                }
            }
        }

        // 校验并绑定GROUP BY字段
        if (selectStatement.getGroupByClause() != null) {
            for (FieldReference fieldRef : selectStatement.getGroupByClause().getFields()) {
                this.resolveAndSetFieldReferenceColumnInfo(fieldRef, "GROUP BY", context);
            }
        }

        // 校验HAVING聚合函数参数字段
        if (selectStatement.getHavingExpression() != null) {
            this.checkHavingExpressionFields(selectStatement.getHavingExpression(), context);
        }
    }

    private void checkHavingExpressionFields(HavingExpression expression, CheckerContext context) {
        if (expression == null || expression.getNodes() == null) {
            return;
        }
        for (HavingConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                this.checkHavingExpressionFields(node.getSubExpression(), context);
            } else if (node.getLeftSide() instanceof com.mybatisgx.dsl.mgxql.model.expression.HavingAggregateExpression) {
                com.mybatisgx.dsl.mgxql.model.expression.HavingAggregateExpression aggExpr =
                        (com.mybatisgx.dsl.mgxql.model.expression.HavingAggregateExpression) node.getLeftSide();
                String argument = aggExpr.getArgument();
                if (argument == null || isAggregateConventionValue(aggExpr.getArgumentKind())) {
                    continue;
                }
                String entityAlias = null;
                String fieldName = argument;
                int dotIndex = argument.indexOf('.');
                if (dotIndex > 0) {
                    entityAlias = argument.substring(0, dotIndex);
                    fieldName = argument.substring(dotIndex + 1);
                }
                this.checkFieldExistence(entityAlias, fieldName, "HAVING", context);
            }
        }
    }

    private static boolean isAggregateConventionValue(AggregateArgumentKind kind) {
        return kind == AggregateArgumentKind.ASTERISK || kind == AggregateArgumentKind.NUMBER;
    }

    /**
     * 校验聚合函数与普通查询字段不可共存
     */
    private void checkAggregateCoexistence(SelectStatement selectStatement, CheckerContext context) {
        if (selectStatement.getSelectItems() == null) {
            return;
        }
        boolean hasAggregate = false;
        boolean hasColumn = false;
        for (SelectItem selectItem : selectStatement.getSelectItems()) {
            if (selectItem.getType() == SelectItemType.COLUMN || selectItem.getType() == SelectItemType.COLUMN_ALL) {
                hasColumn = true;
            } else {
                hasAggregate = true;
            }
        }
        if (hasAggregate && hasColumn) {
            context.addError("聚合函数与普通查询字段不可共存于同一 SELECT 子句");
        }
    }
}
