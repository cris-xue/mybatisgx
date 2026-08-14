package com.mybatisgx.dsl.mgxql.model;

import java.util.Collections;
import java.util.List;

/**
 * {@code #when(expr)[body]} 分支（{@link ChooseNode} 的分支，design D2）。
 * <p>{@link #guard} 为 OGNL 原始字符串（截取透传，不解析、不校验，design D8）；
 * body 为平面 {@link WhereExpression}。{@code #choose} 内只含 {@code #when} 与 {@code #otherwise}。
 *
 * @author 薛承城
 * @description choose 分支节点
 * @date 2026/7/19
 */
public class WhenNode extends WhereElement {

    /**
     * guard 表达式原始字符串（截取，不解析）。
     */
    private String guard;

    private WhereExpression body;

    public WhenNode() {
    }

    public WhenNode(String guard, WhereExpression body) {
        this.guard = guard;
        this.body = body;
    }

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    public WhereExpression getBody() {
        return body;
    }

    public void setBody(WhereExpression body) {
        this.body = body;
    }

    @Override
    public List<WhereExpression> getChildExpressions() {
        return body != null ? Collections.singletonList(body) : Collections.emptyList();
    }
}
