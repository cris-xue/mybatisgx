package com.mybatisgx.dsl.mgxsql.model.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql {@code #otherwise[body]} 兜底分支节点（Unit 层），对应 MyBatis {@code <otherwise>}。
 *
 * @author 薛承城
 * @description choose-otherwise 兜底分支 AST 节点
 * @date 2026/7/13
 */
public class OtherwiseUnit extends AbstractMgxsqlNode {

    /**
     * 兜底分支体子节点序列
     */
    private final List<MgxsqlNode> body = new ArrayList<MgxsqlNode>();

    public OtherwiseUnit(int startPosition, int line, int column) {
        super(startPosition, line, column);
    }

    public List<MgxsqlNode> getBody() {
        return body;
    }
}
