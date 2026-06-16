package com.mybatisgx.dsl.method.model;

import com.mybatisgx.model.ComparisonOperator;
import com.mybatisgx.model.LogicOperator;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2026/6/8 9:49
 */
public class ConditionTerm {

    private LogicOperator logicOperator;

    private String fieldName;

    private ComparisonOperator comparisonOperator;

    private String value;

    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public ComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(ComparisonOperator comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toConditionTerm() {
        if (comparisonOperator != null && comparisonOperator.isNullComparisonOperator()) {
            return String.format("%s %s %s", logicOperator.getValue(), fieldName, comparisonOperator.getValue());
        }
        return String.format("%s %s %s %s", logicOperator.getValue(), fieldName, comparisonOperator.getValue(), value);
    }
}
