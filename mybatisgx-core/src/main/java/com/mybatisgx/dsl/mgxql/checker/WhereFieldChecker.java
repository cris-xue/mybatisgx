package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.dsl.mgxql.model.MgxqlStatement;

/**
 * 字段存在性校验器
 * <p>
 * 校验SELECT字段、WHERE条件字段、ORDER BY字段、GROUP BY字段是否存在于对应实体中
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class WhereFieldChecker extends FieldChecker {

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean support(MgxqlStatement mgxqlStatement) {
        return true;
    }

    @Override
    public void check(MgxqlStatement mgxqlStatement, CheckerContext context) {
        // 校验WHERE条件字段
        if (mgxqlStatement.getWhereClause() != null && mgxqlStatement.getWhereClause().getRootExpression() != null) {
            this.checkConditionExpressionFields(mgxqlStatement.getWhereClause().getRootExpression(), context);
        }
    }
}
