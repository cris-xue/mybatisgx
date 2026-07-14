package com.mybatisgx.dsl.mgxsql.model.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * mgxsql 条件单元块节点（Unit 层），统一表达五种形式：
 * <ul>
 *   <li>{@code #[body]} —— 无 guard，自动 isNotEmpty</li>
 *   <li>{@code #(expr)[body]} —— 有 guard；expr 为空时退化为自动 isNotEmpty</li>
 *   <li>{@code #condition} —— 形式1单条件简写</li>
 *   <li>{@code #and condition} / {@code #or condition} —— 形式1带前缀</li>
 *   <li>{@code #, condition} —— SET 域逗号前缀形式1</li>
 * </ul>
 * <p>{@link #guardExpression} 为自定义 guard（{@code #(expr)} 中非空的 expr）；
 * 为 null 或空白时由 Renderer 从 {@link #body} 直接子级收集参数路径生成 {@code isNotEmpty(...)} test。
 * 形式1的 and/or/逗号前缀由 Parser 作为 body 首段文本注入，节点本身不单独持有前缀字段。
 *
 * @author 薛承城
 * @description if 条件单元块 AST 节点
 * @date 2026/7/13
 */
public class IfUnit extends AbstractMgxsqlNode {

    /**
     * 自定义 guard 表达式原文（{@code #(expr)} 的 expr）；null 或空白表示无自定义 guard
     */
    private final String guardExpression;

    /**
     * 条件体子节点序列
     */
    private final List<MgxsqlNode> body = new ArrayList<MgxsqlNode>();

    public IfUnit(String guardExpression, int startPosition, int line, int column) {
        super(startPosition, line, column);
        this.guardExpression = guardExpression;
    }

    public String getGuardExpression() {
        return guardExpression;
    }

    /**
     * 是否为自定义 guard（非 null 且非空白）。false 时 Renderer 走自动 isNotEmpty。
     */
    public boolean hasCustomGuard() {
        return guardExpression != null && !guardExpression.trim().isEmpty();
    }

    public List<MgxsqlNode> getBody() {
        return body;
    }
}
