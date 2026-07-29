package com.mybatisgx.dsl.mgxql.model;

import java.util.Collections;
import java.util.List;

/**
 * 无 guard 动态条件门（mgxsql 子集 {@code #[body]}），auto isNotEmpty guard 由 mgxsql 在消费阶段计算（design D5）。
 * <p>body 为平面 {@link WhereExpression}（普通条件 + SQL 括号分组，不支持动态门嵌套，design D2）。
 * 渲染时 {@link #logicOperator} 还原前缀：AND→{@code #[and ...]}、OR→{@code #[or ...]}、NULL→{@code #[...]}（design D9）。
 *
 * @author 薛承城
 * @description 无 guard 动态条件门
 * @date 2026/7/19
 */
public class BracketDirectiveNode extends WhereElement {

    private WhereExpression body;

    public BracketDirectiveNode() {
    }

    public BracketDirectiveNode(WhereExpression body) {
        this.body = body;
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
