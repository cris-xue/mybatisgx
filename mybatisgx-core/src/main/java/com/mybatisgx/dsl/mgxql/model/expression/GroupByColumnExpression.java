package com.mybatisgx.dsl.mgxql.model.expression;

/**
 * GROUP BY 中的单列表达式
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class GroupByColumnExpression extends GroupBySqlExpression {

    private final String dbColumnName;

    public GroupByColumnExpression(String dbColumnName) {
        this.dbColumnName = dbColumnName;
    }

    @Override
    public String toSql() {
        return dbColumnName;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }
}
