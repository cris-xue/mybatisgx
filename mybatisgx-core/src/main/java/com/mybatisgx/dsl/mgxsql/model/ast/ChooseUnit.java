package com.mybatisgx.dsl.mgxsql.model.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql {@code #choose[...]} 多分支互斥选择节点（Unit 层），对应 MyBatis {@code <choose>}。
 * <p>聚合若干 {@link WhenUnit}（至少 1 个，必须带 guard）和可选的 {@link OtherwiseUnit}（0 或 1 个）。
 *
 * @author 薛承城
 * @description choose 多分支选择 AST 节点
 * @date 2026/7/13
 */
public class ChooseUnit extends AbstractMgxsqlNode {

    /**
     * when 分支序列（每个必须带 guard）
     */
    private final List<WhenUnit> whens = new ArrayList<WhenUnit>();

    /**
     * otherwise 兜底分支，可为 null
     */
    private OtherwiseUnit otherwise;

    public ChooseUnit(int startPosition, int line, int column) {
        super(startPosition, line, column);
    }

    public List<WhenUnit> getWhens() {
        return whens;
    }

    public OtherwiseUnit getOtherwise() {
        return otherwise;
    }

    public void setOtherwise(OtherwiseUnit otherwise) {
        this.otherwise = otherwise;
    }
}
