package com.mybatisgx.dsl.mgxql.model;

import java.util.List;

/**
 * MGXQL HAVING条件模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class HavingCondition {

    /**
     * 聚合函数（如 max(age)、count(id)）
     */
    private SelectItem aggregateFunction;

    /**
     * 比较运算符（>、<、= 等）
     */
    private ComparisonOperator operator;

    /**
     * 右侧参数值路径
     */
    private List<String> paramValuePath;

    /**
     * 右侧数字字面量值（如 having count(id) > 10 中的 10）
     */
    private Integer havingValue;

    public SelectItem getAggregateFunction() {
        return aggregateFunction;
    }

    public void setAggregateFunction(SelectItem aggregateFunction) {
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

    public Integer getHavingValue() {
        return havingValue;
    }

    public void setHavingValue(Integer havingValue) {
        this.havingValue = havingValue;
    }
}
