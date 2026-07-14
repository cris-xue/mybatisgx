package com.mybatisgx.dsl.mgxsql.model.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql {@code #when(expr)[body]} 分支节点（Unit 层），对应 MyBatis {@code <when>}。
 * <p>guard 复用 {@code #(expr)} 规则（{@code :param} 去冒号、{@code < > && ||} 转义），必须非空。
 *
 * @author 薛承城
 * @description choose-when 分支 AST 节点
 * @date 2026/7/13
 */
public class WhenUnit extends AbstractMgxsqlNode {

    /**
     * guard 表达式原文（必填，Renderer 应用 stripParamColons 后写入 test）
     */
    private final String guardExpression;

    /**
     * 分支体子节点序列
     */
    private final List<MgxsqlNode> body = new ArrayList<MgxsqlNode>();

    public WhenUnit(String guardExpression, int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.guardExpression = guardExpression;
    }

    public String getGuardExpression() {
        return guardExpression;
    }

    public List<MgxsqlNode> getBody() {
        return body;
    }
}
