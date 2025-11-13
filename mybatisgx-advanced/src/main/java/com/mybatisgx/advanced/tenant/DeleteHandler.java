package com.mybatisgx.advanced.tenant;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteHandler {

    public String processDelete(Delete delete, String tenantId) {
        // 构建租户条件
        EqualsTo tenantCondition = new EqualsTo(
                new Column("tenant_id"),
                new StringValue(tenantId)
        );

        // 合并WHERE条件
        Expression where = delete.getWhere();
        if (where == null) {
            delete.setWhere(tenantCondition);
        } else {
            delete.setWhere(new AndExpression(where, tenantCondition));
        }

        return delete.toString();
    }
}
