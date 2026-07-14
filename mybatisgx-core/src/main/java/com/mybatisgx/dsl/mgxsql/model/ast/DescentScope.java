package com.mybatisgx.dsl.mgxsql.model.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 容器下沉语法域节点（Scope 层），对应原生 {@code <where>}/{@code <set>}/{@code <trim>} 三标签。
 * <p>三标签开闭标签（含 {@code <trim>} 的 prefix/suffixOverrides 等属性）原样透传，标签内部文本走中性翻译。
 * 本节点记录开标签与匹配的闭标签，渲染期原样输出开标签 + 子节点翻译 + 闭标签。
 *
 * @author 薛承城
 * @description 三标签容器下沉 AST 节点
 * @date 2026/7/13
 */
public class DescentScope extends AbstractMgxsqlNode {

    /**
     * 原生开标签全文（含属性），如 {@code <trim prefix="SET" suffixOverrides=",">}
     */
    private final String openTag;

    /**
     * 匹配的闭标签，如 {@code </trim>}
     */
    private final String closeTag;

    /**
     * 容器内部子节点序列（中性翻译）
     */
    private final List<MgxsqlNode> children = new ArrayList<MgxsqlNode>();

    public DescentScope(String openTag, String closeTag, int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.openTag = openTag;
        this.closeTag = closeTag;
    }

    public String getOpenTag() {
        return openTag;
    }

    public String getCloseTag() {
        return closeTag;
    }

    public List<MgxsqlNode> getChildren() {
        return children;
    }
}
