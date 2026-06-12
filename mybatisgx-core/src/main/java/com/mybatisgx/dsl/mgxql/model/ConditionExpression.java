package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL条件表达式模型，表示一层逻辑表达式（AND层或OR层）
 * <p>
 * 结构：node1 AND/OR node2 AND/OR node3 ...
 * 每个 node 可以是基础条件或嵌套的括号表达式
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class ConditionExpression {

    /**
     * 节点间逻辑关系（AND/OR）
     */
    private LogicOperator logicOperator;

    /**
     * 条件节点列表
     */
    private List<ConditionNode> nodes = new ArrayList<>();

    public ConditionExpression() {
    }

    public ConditionExpression(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    public List<ConditionNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ConditionNode> nodes) {
        this.nodes = nodes;
    }

    public void addNode(ConditionNode node) {
        this.nodes.add(node);
    }
}
