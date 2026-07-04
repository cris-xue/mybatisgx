package com.mybatisgx.dsl.mgxql.model.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * WHERE 条件中的复合主键表达式，包含多个子列
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class ConditionCompositeExpression extends ConditionSqlExpression {

    private final List<ConditionColumnExpression> columns;

    public ConditionCompositeExpression(List<ConditionColumnExpression> columns) {
        this.columns = columns != null ? columns : new ArrayList<>();
    }

    @Override
    public String toSql() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(columns.get(i).toSql());
        }
        return sb.toString();
    }

    public List<ConditionColumnExpression> getColumns() {
        return columns;
    }
}
