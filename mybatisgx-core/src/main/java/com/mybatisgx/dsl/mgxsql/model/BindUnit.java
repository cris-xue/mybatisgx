package com.mybatisgx.dsl.mgxsql.model;

/**
 * mgxsql bind 单元块节点（Unit 层），对应 MyBatis {@code <bind>}，表达 LIKE 模式。
 * <p>三种 LIKE 模式由 {@code bindValue} 表达式区分：
 * <ul>
 *   <li>双侧 {@code %:name%} —— {@code '%' + name + '%'}</li>
 *   <li>右侧 {@code :name%} —— {@code name + '%'}</li>
 *   <li>左侧 {@code %:name} —— {@code '%' + name}</li>
 * </ul>
 * {@code paramName} 参与外层条件体的参数收集；{@code emitReference} 控制渲染时是否在
 * {@code <bind>} 之后追加 {@code #{bindName}} 引用（条件体内追加，作用域顶层不追加）。
 *
 * @author 薛承城
 * @description bind/LIKE 单元块 AST 节点
 * @date 2026/7/13
 */
public class BindUnit extends AbstractMgxsqlNode {

    /**
     * 原始参数名（如 name），用于参数收集
     */
    private final String paramName;

    /**
     * 绑定变量名（如 _like_name）
     */
    private final String bindName;

    /**
     * 绑定值表达式
     */
    private final String bindValue;

    /**
     * 渲染时是否在 <bind> 后追加 #{bindName} 引用
     */
    private final boolean emitReference;

    public BindUnit(String paramName, String bindName, String bindValue, boolean emitReference,
                    int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.paramName = paramName;
        this.bindName = bindName;
        this.bindValue = bindValue;
        this.emitReference = emitReference;
    }

    public String getParamName() {
        return paramName;
    }

    public String getBindName() {
        return bindName;
    }

    public String getBindValue() {
        return bindValue;
    }

    public boolean isEmitReference() {
        return emitReference;
    }
}
