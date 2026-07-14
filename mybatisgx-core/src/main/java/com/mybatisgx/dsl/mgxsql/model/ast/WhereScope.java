package com.mybatisgx.dsl.mgxsql.model.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql WHERE 语法域节点（Scope 层），对应 {@code where} / {@code where[...]}。
 * <p>{@link #bounded} 区分有边界形式 {@code where[...]}（由 {@code ]} 关闭）与无边界形式
 * {@code where}（到子句关键字或末尾关闭）。该差异仅影响解析期作用域边界，渲染期统一输出
 * {@code <where>...</where>}。
 *
 * @author 薛承城
 * @description WHERE 语法域 AST 节点
 * @date 2026/7/13
 */
public class WhereScope extends AbstractMgxsqlNode {

    /**
     * 是否为有边界形式 where[...]
     */
    private final boolean bounded;

    /**
     * 域内子节点序列（Unit / Expression 混合）
     */
    private final List<MgxsqlNode> children = new ArrayList<MgxsqlNode>();

    public WhereScope(boolean bounded, int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.bounded = bounded;
    }

    public boolean isBounded() {
        return bounded;
    }

    public List<MgxsqlNode> getChildren() {
        return children;
    }
}
