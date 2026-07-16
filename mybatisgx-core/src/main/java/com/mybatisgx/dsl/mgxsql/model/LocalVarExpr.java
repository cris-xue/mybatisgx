package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql 局部变量表达式节点（Expression 层），对应 {@code $variable}。
 * <p>出现在：{@code =>} 右侧（复杂类型 IN / {@code #for} 的迭代值）、显式 {@code #bind} 的引用处
 * （如 {@code id = $age2}）。渲染为 {@code #{varName}}。局部变量引用不参与 auto-guard 收集。
 *
 * @author 薛承城
 * @description $variable 局部变量 AST 节点
 * @date 2026/7/13
 */
public class LocalVarExpr extends AbstractMgxsqlNode {

    /**
     * 变量名（如 item、item.id）
     */
    private final String varName;

    public LocalVarExpr(String varName, int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }
}
