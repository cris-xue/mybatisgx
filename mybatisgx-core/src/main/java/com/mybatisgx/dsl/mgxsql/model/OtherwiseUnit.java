package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql {@code #otherwise[body]} 兜底分支节点（Unit 层），对应 MyBatis {@code <otherwise>}。
 *
 * @author 薛承城
 * @description choose-otherwise 兜底分支 AST 节点
 * @date 2026/7/13
 */
public class OtherwiseUnit extends AbstractMgxsqlContainerUnit {

    public OtherwiseUnit(int startPosition, int line, int column) {
        super(startPosition, line, column);
    }
}
