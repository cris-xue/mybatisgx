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

        // 校验SELECT字段
        if (selectStatement.getSelectItems() != null) {
            for (SelectItem selectItem : selectStatement.getSelectItems()) {
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

        // 校验ORDER BY字段
        if (selectStatement.getOrderByClause() != null) {
            for (OrderByItem item : selectStatement.getOrderByClause().getItems()) {
                if (item.getField() != null) {
                    this.checkFieldExistence(item.getField().getEntityAlias(), item.getField().getFieldName(), "ORDER BY", context);
                }
            }
        }

        // 校验GROUP BY字段
        if (selectStatement.getGroupByClause() != null) {
            for (FieldReference fieldRef : selectStatement.getGroupByClause().getFields()) {
                this.checkFieldExistence(fieldRef.getEntityAlias(), fieldRef.getFieldName(), "GROUP BY", context);
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
                    this.checkFieldExistence(fieldRef.getEntityAlias(), fieldRef.getFieldName(), "HAVING", context);
                }
            }
        }
    }

    private static boolean isCountConventionValue(String fieldName) {
        return "*".equals(fieldName) || "1".equals(fieldName);
    }
}
