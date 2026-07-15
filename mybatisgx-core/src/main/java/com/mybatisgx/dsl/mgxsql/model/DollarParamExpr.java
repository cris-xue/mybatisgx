package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql MyBatis 原生替换语法表达式节点（Expression 层），对应 {@code ${param}}。
 * <p>仅在范围块（Scope）内合法并原样保留；在条件节点块内出现时由 Parser 报语法错误。
 * 渲染为 {@code ${content}}。
 *
 * @author 薛承城
 * @description ${param} 原生替换语法 AST 节点
 * @date 2026/7/13
 */
public class DollarParamExpr extends AbstractMgxsqlNode {

    /**
     * ${ 与 } 之间的原文
     */
    private final String content;

    public DollarParamExpr(String content, int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
