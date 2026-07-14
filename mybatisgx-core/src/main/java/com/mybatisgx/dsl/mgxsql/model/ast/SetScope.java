package com.mybatisgx.dsl.mgxsql.model.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql SET 语法域节点（Scope 层），对应 {@code set} / {@code set[...]}。
 * <p>{@link #bounded} 区分有边界形式 {@code set[...]}（由 {@code ]} 关闭）与无边界形式
 * {@code set}（到 {@code where} 或末尾关闭）。渲染期统一输出 {@code <set>...</set>}。
 *
 * @author 薛承城
 * @description SET 语法域 AST 节点
 * @date 2026/7/13
 */
public class SetScope extends AbstractMgxsqlNode {

    /**
     * 是否为有边界形式 set[...]
     */
    private final boolean bounded;

    /**
     * 域内子节点序列
     */
    private final List<MgxsqlNode> children = new ArrayList<MgxsqlNode>();

    public SetScope(boolean bounded, int startPosition, int line, int column) {
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
