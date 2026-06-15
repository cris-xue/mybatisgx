package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * WHERE条件表达式模型，表示一层逻辑表达式（AND层或OR层）
 * <p>
 * 结构：node1 AND/OR node2 AND/OR node3 ...
 * 每个 node 可以是基础条件或嵌套的括号表达式
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class WhereExpression {

    private LogicOperator logicOperator;

    private List<WhereConditionNode> nodes = new ArrayList<>();

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

    public List<WhereConditionNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<WhereConditionNode> nodes) {
        this.nodes = nodes;
    }

    public void addNode(WhereConditionNode node) {
        this.nodes.add(node);
    }
}
