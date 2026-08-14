package com.mybatisgx.dsl.mgxql.model;

import java.util.Collections;
import java.util.List;

/**
 * WHERE 元素基类，统一承载普通条件与动态门变体（平面 WhereExpression 序列的元素）。
 * <p>
 * 变体（design D2，不支持动态门嵌套）：
 * <ul>
 *   <li>{@link WhereConditionNode} —— 普通条件（含 SQL 括号分组 subExpression）</li>
 *   <li>{@link BracketDirectiveNode} —— {@code #[body]}</li>
 *   <li>{@link IfDirectiveNode} —— {@code #if(expr)[body]}</li>
 *   <li>{@link ChooseNode} —— {@code #choose[#when(expr)[body]+ #otherwise[body]?]}</li>
 *   <li>{@link WhenNode} —— {@code #when(expr)[body]}（ChooseNode 的分支）</li>
 * </ul>
 * 每个元素持 {@link #logicOperator}（与前一元素的连接：NULL/AND/OR），用于渲染时还原本子集文本的前缀 {@code #[and ...]} / {@code #[or ...]}（design D9）。
 *
 * @author 薛承城
 * @description WHERE 元素平面变体基类
 * @date 2026/7/19
 */
public abstract class WhereElement {

    /**
     * 与前一元素的逻辑连接（NULL/AND/OR）。NULL 表示序列首个元素。
     */
    private LogicOperator logicOperator = LogicOperator.NULL;

    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    /**
     * 是否为普通条件（{@link WhereConditionNode}），用于 checker/bind/render 的普通条件分支判定。
     * 动态门变体返回 false，由 {@link #getChildExpressions()} 暴露其 body 供递归遍历。
     *
     * @return true 表该元素本身是一个普通条件，可直接取 fieldRef/operator/paramValuePath 等。
     */
    public boolean isCondition() {
        return false;
    }

    /**
     * 返回该元素所包含的子 {@link WhereExpression}，供 checker/bind/render 递归遍历（与现有 recursion subExpression 同构，design 2.6/D2）。
     * <ul>
     *   <li>{@link WhereConditionNode}：嵌套时返回 [subExpression]，否则空</li>
     *   <li>{@link BracketDirectiveNode} / {@link IfDirectiveNode} / {@link WhenNode}：返回 [body]</li>
     *   <li>{@link ChooseNode}：返回 [whenBody..., otherwiseBody?]</li>
     * </ul>
     * 注意：动态门 body 为平面 {@link WhereExpression}（design D2，不支持嵌套），故递归天然一层即到底。
     *
     * @return 不可 null，无子表达式时返回空列表
     */
    public List<WhereExpression> getChildExpressions() {
        return Collections.emptyList();
    }

    /**
     * 当且仅当本元素是 {@link WhereConditionNode} 时返回自身，否则 null。便于 checker/bind/render 的窄化。
     */
    public WhereConditionNode asCondition() {
        return null;
    }
}
