package com.mybatisgx.dsl.mgxql.model.expression;

/**
 * WHERE 条件中的单列表达式
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class ConditionColumnExpression extends ConditionSqlExpression {

    private final String dbColumnName;

    private final String typeHandler;

    public ConditionColumnExpression(String dbColumnName, String typeHandler) {
        this.dbColumnName = dbColumnName;
        this.typeHandler = typeHandler;
    }

    public ConditionColumnExpression(String dbColumnName) {
        this(dbColumnName, null);
    }

    @Override
    public String toSql() {
        return dbColumnName;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public String getTypeHandler() {
        return typeHandler;
    }
}
