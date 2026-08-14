package com.mybatisgx.dsl.mgxql.model;

import java.util.Collections;
import java.util.List;

/**
 * 有 guard 的动态条件门（mgxql 文法 {@code #if(expr)[body]}）。
 * <p>{@link #guard} 为 OGNL 表达式原始字符串（吞噬式 lexer mode 截取，design D8/Q5）——存 String、不解析、不做参数校验；
 * 渲染时原样透传到子集文本，去冒号与 {@code &&}/{@code ||}/{@code <}/{@code >} 转义由 mgxsql 在消费阶段完成（design D11）。
 * <p>body 为平面 {@link WhereExpression}（design D2，不支持动态门嵌套）。{@link #logicOperator} 还原 {@code #[and ...]}/{@code #[or ...]} 前缀（design D9）。
 *
 * @author 薛承城
 * @description 有 guard 动态条件门
 * @date 2026/7/19
 */
public class IfDirectiveNode extends WhereElement {

    /**
     * guard 表达式原始字符串（截取，不解析）。空为非法（grammar 报错）。
     */
    private String guard;

    private WhereExpression body;

    public IfDirectiveNode() {
    }

    public IfDirectiveNode(String guard, WhereExpression body) {
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
