package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql 透传文本节点（Expression 层），承载所有无需翻译、原样输出的静态文本：
 * SQL 本体、字符串字面量、原生 XML 片段、空白等。是 AST 中数量最多的节点类型。
 *
 * @author 薛承城
 * @description 静态透传文本 AST 节点
 * @date 2026/7/13
 */
public class PassthroughText extends AbstractMgxsqlNode {

    /**
     * 透传文本原文
     */
    private final String text;

    public PassthroughText(String text, int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
