package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql 扫描状态枚举（v2）
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
     * XML 标签透传：< 开头的标签原样输出
     */
    XML_TAG,

    /**
     * 字符串字面量：单引号内跳过所有 mgxsql 语法
     */
    STRING_LITERAL
}
