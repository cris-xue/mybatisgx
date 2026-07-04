package com.mybatisgx.dsl.method.model;

public class OrderByTerm {

    private String fieldName;

    private String direction;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String toOrderByTerm() {
        return String.format("%s %s", fieldName, direction);
    }
}
