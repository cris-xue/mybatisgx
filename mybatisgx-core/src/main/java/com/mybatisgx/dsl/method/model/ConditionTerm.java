package com.mybatisgx.dsl.method.model;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2026/6/8 9:49
 */
public class ConditionTerm {

    private String fieldName;

    private String operator;

    private String value;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toConditionTerm() {
        return String.format("%s %s %s", fieldName, operator, value);
    }
}
