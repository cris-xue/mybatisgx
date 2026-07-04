package com.mybatisgx.dsl.mgxql.model;

import com.mybatisgx.dsl.mgxql.model.expression.HavingSqlExpression;

import java.util.List;

/**
 * HAVING条件节点模型，支持叶节点和嵌套括号子表达式
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class HavingConditionNode {

    private LogicOperator logicOperator = LogicOperator.NULL;

    private HavingSqlExpression leftSide;

    private ComparisonOperator operator;

    private List<String> paramValuePath;

    private Integer literalValue;

    private BoundParam boundParam;

    private HavingExpression subExpression;

    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    public HavingSqlExpression getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(HavingSqlExpression leftSide) {
        this.leftSide = leftSide;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public void setOperator(ComparisonOperator operator) {
        this.operator = operator;
    }

    public List<String> getParamValuePath() {
        return paramValuePath;
    }

    public void setParamValuePath(List<String> paramValuePath) {
        this.paramValuePath = paramValuePath;
    }

    public Integer getLiteralValue() {
        return literalValue;
    }

    public void setLiteralValue(Integer literalValue) {
        this.literalValue = literalValue;
    }

    public BoundParam getBoundParam() {
        return boundParam;
    }

    public void setBoundParam(BoundParam boundParam) {
        this.boundParam = boundParam;
    }

    public HavingExpression getSubExpression() {
        return subExpression;
    }

    public void setSubExpression(HavingExpression subExpression) {
        this.subExpression = subExpression;
    }

    public boolean isNested() {
        return subExpression != null;
    }
}
