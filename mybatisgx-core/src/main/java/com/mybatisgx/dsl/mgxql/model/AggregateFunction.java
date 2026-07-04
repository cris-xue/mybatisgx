package com.mybatisgx.dsl.mgxql.model;

/**
 * 聚合函数枚举，仅包含聚合类型
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public enum AggregateFunction {

    COUNT("count"),
    MAX("max"),
    MIN("min"),
    AVG("avg"),
    SUM("sum");

    private final String sqlKeyword;

    AggregateFunction(String sqlKeyword) {
        this.sqlKeyword = sqlKeyword;
    }

    public String getSqlKeyword() {
        return sqlKeyword;
    }
}
