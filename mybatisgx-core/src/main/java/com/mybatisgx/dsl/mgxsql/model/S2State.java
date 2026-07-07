package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql 扫描状态枚举
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public enum S2State {

    /**
     * 默认状态，原样输出
     */
    NORMAL,

    /**
     * WHERE 域：where 关键字后的条件区域
     */
    WHERE,

    /**
     * SET 域：set 关键字后的赋值区域
     */
    SET,

    /**
     * 简单可选条件：?字段名 开头
     */
    OPTIONAL_SIMPLE,

    /**
     * 表达式可选条件：?(expr)(condition)
     */
    OPTIONAL_EXPR,

    /**
     * LIKE 模式：%#{x}%、#{x}%、%#{x}
     */
    LIKE_PATTERN,

    /**
     * XML 标签透传：< 开头的标签原样输出
     */
    XML_TAG,

    /**
     * 字符串字面量：单引号内跳过所有 mgxsql 语法
     */
    STRING_LITERAL
}
