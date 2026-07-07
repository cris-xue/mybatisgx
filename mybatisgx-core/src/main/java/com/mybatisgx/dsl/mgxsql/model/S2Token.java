package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql token 模型
 *
 * @author 薛承城
 * @date 2026/7/7
 */
public class S2Token {

    /**
     * token 类型
     */
    private S2TokenType type;

    /**
     * token 文本
     */
    private String text;

    /**
     * token 在输入文本中的起始位置
     */
    private int position;

    public S2Token(S2TokenType type, String text, int position) {
        this.type = type;
        this.text = text;
        this.position = position;
    }

    public S2TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getPosition() {
        return position;
    }

    /**
     * mgxsql token 类型枚举
     */
    public enum S2TokenType {

        /**
         * 静态文本（原样输出）
         */
        STATIC_TEXT,

        /**
         * where 关键字
         */
        WHERE,

        /**
         * set 关键字
         */
        SET,

        /**
         * 简单可选条件：?条件
         */
        OPTIONAL_SIMPLE,

        /**
         * 表达式可选条件：?(expr)(condition)
         */
        OPTIONAL_EXPR,

        /**
         * 简单类型 IN：in #{list}
         */
        IN_SIMPLE,

        /**
         * 复杂类型 IN：in (item:collection)=>#{item.prop}
         */
        IN_COMPLEX,

        /**
         * LIKE 模式：%#{x}%、#{x}%、%#{x}
         */
        LIKE_PATTERN,

        /**
         * MyBatis 参数引用：#{param}
         */
        PARAM_REF,

        /**
         * XML 标签
         */
        XML_TAG,

        /**
         * and 逻辑连接词
         */
        AND,

        /**
         * or 逻辑连接词
         */
        OR,

        /**
         * 字符串字面量
         */
        STRING_LITERAL
    }
}
