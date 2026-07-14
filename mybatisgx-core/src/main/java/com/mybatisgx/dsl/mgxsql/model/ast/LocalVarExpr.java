package com.mybatisgx.dsl.mgxsql.model.ast;

/**
 * mgxsql 局部变量表达式节点（Expression 层），对应 {@code $variable}。
 * <p>仅出现在 {@code =>} 右侧（复杂类型 IN 的迭代值）。渲染为 {@code #{varName}}。
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
