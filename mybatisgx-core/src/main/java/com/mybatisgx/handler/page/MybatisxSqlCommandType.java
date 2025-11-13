package com.mybatisgx.handler.page;

public enum MybatisxSqlCommandType {

    UPDATE, QUERY;

    public boolean equalsIgnoreCase(String methodName) {
        return this.name().equalsIgnoreCase(methodName);
    }
}
