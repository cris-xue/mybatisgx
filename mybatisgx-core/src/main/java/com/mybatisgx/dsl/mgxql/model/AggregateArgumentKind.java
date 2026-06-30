package com.mybatisgx.dsl.mgxql.model;

/**
 * 聚合函数参数类型枚举
 * <p>
 * 表达 SELECT 与 HAVING 子句中聚合函数参数的形态：
 * FIELD（字段引用）、NUMBER（数字字面量，如 count(1)）、ASTERISK（星号，如 count(*)）。
 * COLUMN 与 COLUMN_ALL 类型的 SelectItem 的 argumentKind 为 null。
 *
 * @author 薛承城
 * @date 2026/6/30
 */
public enum AggregateArgumentKind {

    /**
     * 字段引用参数，如 count(id)、max(age)
     */
    FIELD,
    /**
     * 数字字面量参数，如 count(1)
     */
    NUMBER,
    /**
     * 星号参数，如 count(*)
     */
    ASTERISK
}
