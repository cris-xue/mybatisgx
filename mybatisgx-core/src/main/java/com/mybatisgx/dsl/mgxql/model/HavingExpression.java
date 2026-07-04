package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * HAVING条件表达式模型，表示一层逻辑表达式（AND层或OR层）
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class HavingExpression {

    private LogicOperator logicOperator;

    private List<HavingConditionNode> nodes = new ArrayList<>();

    public HavingExpression() {
    }

    public HavingExpression(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    public List<HavingConditionNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<HavingConditionNode> nodes) {
        this.nodes = nodes;
    }

    public void addNode(HavingConditionNode node) {
        this.nodes.add(node);
    }
}
