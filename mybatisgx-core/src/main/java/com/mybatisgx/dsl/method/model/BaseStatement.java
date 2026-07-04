package com.mybatisgx.dsl.method.model;

import org.apache.ibatis.mapping.SqlCommandType;

public abstract class BaseStatement {

    protected SqlCommandType sqlCommandType;

    protected Condition condition;

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public abstract String toMgxql();
}
