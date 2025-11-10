package com.mybatisgx.sql;

public enum MybatisxSqlCommandType {

    UPDATE, QUERY;

    public boolean equalsIgnoreCase(String methodName) {
        return this.name().equalsIgnoreCase(methodName);
    }
}
