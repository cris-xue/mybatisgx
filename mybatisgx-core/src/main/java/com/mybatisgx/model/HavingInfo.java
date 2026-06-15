package com.mybatisgx.model;

import java.util.List;

/**
 * HAVING 条件目标模型，不复用 ConditionInfo
 *
 * @author 薛承城
 * @date 2026/6/12
 */
@Deprecated
public class HavingInfo {

    private AggregateFunctionInfo aggregateFunction;

    private ComparisonOperator operator;

    private List<String> paramValuePath;

    private Integer literalValue;

    public AggregateFunctionInfo getAggregateFunction() {
        return aggregateFunction;
    }

    public void setAggregateFunction(AggregateFunctionInfo aggregateFunction) {
        this.aggregateFunction = aggregateFunction;
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
}
