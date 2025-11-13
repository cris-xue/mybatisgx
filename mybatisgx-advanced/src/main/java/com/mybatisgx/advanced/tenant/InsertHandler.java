package com.mybatisgx.advanced.tenant;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.Values;

public class InsertHandler {

    public String processInsert(Insert insert, String tenantId) {
        // 添加租户列
        insert.getColumns().add(new Column("tenant_id"));
        Select select = insert.getSelect();
        if (select instanceof Values) {
            Values values = (Values) select;
            Expression expression = new StringValue(tenantId);
            values.addExpressions(expression);
        } else if (insert.getWithItemsList() instanceof LateralSubSelect) {
            // 处理SELECT类型的INSERT
            // 递归处理子查询
        }
        return insert.toString();
    }
}
