package com.mybatisgx.dsl.mgxsql.model.ast;

/**
 * mgxsql 参数绑定表达式节点（Expression 层），对应 {@code :param} / {@code :user.name}。
 * <p>渲染为 {@code #{paramName}}。{@code paramName} 参与所在条件体的参数收集（生成 isNotEmpty）。
 *
 * @author 薛承城
 * @description :param 参数绑定 AST 节点
 * @date 2026/7/13
 */
public class ParamExpr extends AbstractMgxsqlNode {

    /**
     * 参数路径（支持嵌套，如 user.name）
     */
    private final String paramName;

    public ParamExpr(String paramName, int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
