package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql 透传文本节点（Expression 层）的抽象基类，承载所有无需结构化翻译、直接进输出的静态文本。
 * <p>拆分为两个子类（mgxsql-passthrough-text-split），由节点类型决定渲染转义策略：
 * <ul>
 *   <li>{@link SqlText}：SQL 文本片段（SQL 本体、字符串字面量、空白等），渲染时 XML 转义 {@code < > &}</li>
 *   <li>{@link XmlTagText}：嵌入的原生 MyBatis XML 标签（{@code <if>}/{@code <foreach>} 等），渲染时原样输出</li>
 * </ul>
 * 转义决策由节点类型（{@code instanceof}）决定，而非渲染上下文。基类设为 abstract，
 * 强制每个文本片段创建点二选一，避免漏分类。是 AST 中数量最多的节点类型。
 *
 * @author 薛承城
 * @description 静态透传文本 AST 节点（abstract 基类）
 * @date 2026/7/13
 */
public abstract class PassthroughText extends AbstractMgxsqlNode {

    /**
     * 透传文本原文
     */
    private final String text;

    protected PassthroughText(String text, int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
