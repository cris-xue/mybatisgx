package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.*;

/**
 * SELECT星号校验器
 * <p>
 * R8: 多实体时不允许裸select *，必须用alias.*
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class SelectStarChecker implements MgxqlSyntaxChecker {

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public void check(MgxqlStatement statement, SyntaxCheckerContext context) {
        if (!context.isHasMultipleEntities()) {
            return;
        }

        if (statement.getSelectItems() == null) {
            return;
        }

        for (SelectItem selectItem : statement.getSelectItems()) {
            if (selectItem.getType() == SelectItemType.COLUMN_ALL) {
                // 裸 select * (无alias前缀)
                if (selectItem.getEntityAlias() == null || selectItem.getEntityAlias().isEmpty()) {
                    context.addError("多实体查询中，SELECT * 必须指定实体别名，如 user.*");
                }
            }
        }
    }
}
