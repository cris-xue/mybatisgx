package com.mybatisgx.dsl.method.model;

public class ModifyStatement extends BaseStatement {

    protected String entity;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    @Override
    public String toMgxql() {
        String whereSql = "";
        if (condition != null) {
            whereSql = condition.toCondition();
        }

        return String.format(
                "%s %s %s",
                sqlCommandType.name().toLowerCase(),
                entity,
                whereSql
        );
    }
}
