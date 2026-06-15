package com.mybatisgx.dsl.mgxql.model.expression;

/**
 * SQL 表达式接口，所有语义域的 SQL 表达式实现此接口
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public interface SqlExpression {

    /**
     * 渲染为 SQL 片段
     *
     * @return SQL 片段字符串
     */
    String toSql();
}
