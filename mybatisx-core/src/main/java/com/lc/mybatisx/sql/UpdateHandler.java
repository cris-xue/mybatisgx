package com.lc.mybatisx.sql;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;

public class UpdateHandler {

    public String processUpdate(Update update, String tenantId) {
        // 构建租户条件
        EqualsTo tenantCondition = new EqualsTo(
                new Column("tenant_id"),
                new StringValue(tenantId)
        );

        // 合并WHERE条件
        Expression where = update.getWhere();
        if (where == null) {
            update.setWhere(tenantCondition);
        } else {
            update.setWhere(new AndExpression(where, tenantCondition));
        }

        return update.toString();
    }
}
