package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * WHERE条件表达式模型，平面 {@link WhereElement} 序列（design D2）。
 * <p>
 * 结构：element1 AND/OR element2 AND/OR element3 ...，每个 element 携自身 {@link WhereElement#getLogicOperator()}。
 * element 可为普通条件（{@link WhereConditionNode}）或动态门变体（{@link BracketDirectiveNode}/{@link IfDirectiveNode}/{@link ChooseNode}）；
 * 括号分组复用 {@link WhereConditionNode#getSubExpression()}（design Q2）。不支持动态门嵌套（design D2）。
 * <p>
 * <b>顶层 logicOperator 核实（design Q4）</b>：经勘察，现有 {@code MgxqlSyntaxHandler.WhereClauseVisitor}
 * 仅以 {@link LogicOperator#NULL} 创建根表达式，节点间连接由每节点 logicOperator 自表达，
 * 根 {@link #logicOperator} 仅作容器标记、未承担实质语义，保留以兼容旧读法（如 HAVING 同构遍历），不额外承担连接语义。
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class WhereExpression {

    private LogicOperator logicOperator;

    private List<WhereElement> nodes = new ArrayList<>();

    public WhereExpression() {
    }

    public WhereExpression(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    public List<WhereElement> getNodes() {
        return nodes;
    }

    public void setNodes(List<WhereElement> nodes) {
        this.nodes = nodes;
    }

    public void addNode(WhereElement node) {
        this.nodes.add(node);
    }

    /**
     * 便捷添加普通条件节点（兼容旧调用点 {@code addNode(WhereConditionNode)}）。
     */
    public void addNode(WhereConditionNode node) {
        this.nodes.add(node);
    }

    /**
     * 窄类型读取：以 {@link WhereConditionNode} 视角返回所有普通条件节点（跳过动态门变体）。
     * <p>P1 兼容既有调用点（{@code getNodes().get(0)}、{@code for (WhereConditionNode n : getNodes())} 等）：
     * 当 WHERE 仍只产普通条件时，与放宽前完全等价；动态门变体由 {@link #getElements()} / {@link #getNodes()} 暴露。
     * <p><b>注意</b>：动态门变体（Bracket/If/Choose）不在此列表——若旧逻辑假定索引覆盖全部元素，需迁移为遍历 {@link #getNodes()} + instanceof/asCondition。
     */
    public List<WhereConditionNode> getConditions() {
        List<WhereConditionNode> conditions = new ArrayList<WhereConditionNode>();
        for (WhereElement element : this.nodes) {
            if (element.isCondition()) {
                conditions.add(element.asCondition());
            }
        }
        return conditions;
    }
}