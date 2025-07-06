package com.lc.mybatisx.sql;

import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.LateralSubSelect;

public class InsertHandler {

    public String processInsert(Insert insert, String tenantId) {
        // 添加租户列
        insert.getColumns().add(new Column("tenant_id"));
        // 添加租户值
        if (insert.getWithItemsList() instanceof ExpressionList) {
            ExpressionList list = (ExpressionList) insert.getWithItemsList();
            list.getExpressions().add(new StringValue(tenantId));
        } else if (insert.getWithItemsList() instanceof LateralSubSelect) {
            // 处理SELECT类型的INSERT
            // 递归处理子查询
        }
        return insert.toString();
    }
}
