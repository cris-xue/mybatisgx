package com.mybatisgx.dsl.mgxsql.model.ast;

/**
 * mgxsql AST 节点抽象基类，集中维护位置信息，子类只需声明各自的语义字段。
 *
 * @author 薛承城
 * @description AST 节点位置基类
 * @date 2026/7/13
 */
public abstract class AbstractMgxsqlNode implements MgxsqlNode {

    private final int startPosition;
    private final int line;
    private final int column;

    protected AbstractMgxsqlNode(int startPosition, int line, int column) {
        this.startPosition = startPosition;
        this.line = line;
        this.column = column;
    }

    @Override
    public int getStartPosition() {
        return startPosition;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }
}
