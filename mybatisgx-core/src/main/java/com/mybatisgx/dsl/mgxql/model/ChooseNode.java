package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 多分支互斥动态门（mgxql 子集 {@code #choose[#when(expr)[body]+ #otherwise[body]?]}）。
 * <p>{@link #whens} 为 {@link WhenNode} 列表（一到多个），{@link #otherwise} 可空。
 * body 仍是平面 {@link WhereExpression}（design D2，不支持动态门嵌套）。
 * {@link #logicOperator} 还原 {@code #[and ...]}/{@code #[or ...]} 前缀（design D9）。
 *
 * @author 薛承城
 * @description multi-branch choose 节点
 * @date 2026/7/19
 */
public class ChooseNode extends WhereElement {

    private List<WhenNode> whens = new ArrayList<>();

    private WhereExpression otherwise;

    public ChooseNode() {
    }

    public List<WhenNode> getWhens() {
        return whens;
    }

    public void setWhens(List<WhenNode> whens) {
        this.whens = whens != null ? whens : new ArrayList<WhenNode>();
    }

    public void addWhen(WhenNode when) {
        this.whens.add(when);
    }

    public WhereExpression getOtherwise() {
        return otherwise;
    }

    public void setOtherwise(WhereExpression otherwise) {
        this.otherwise = otherwise;
    }

    /**
     * 暴露各 when 与 otherwise 的 body 供 checker/bind/render 递归遍历（design D2/2.6）。
     */
    @Override
    public List<WhereExpression> getChildExpressions() {
        List<WhereExpression> children = new ArrayList<WhereExpression>();
        for (WhenNode when : whens) {
            if (when.getBody() != null) {
                children.add(when.getBody());
            }
        }
        if (otherwise != null) {
            children.add(otherwise);
        }
        return children;
    }
}
