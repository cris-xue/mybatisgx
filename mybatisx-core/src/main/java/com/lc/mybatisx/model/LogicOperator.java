package com.lc.mybatisx.model;

public enum LogicOperator {

    AND("And", "And"),
    OR("Or", "Or");

    private String key;
    private String value;

    LogicOperator(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static LogicOperator getLogicOperator(String key) {
        for (LogicOperator logicOperator : LogicOperator.values()) {
            if (logicOperator.key.equals(key)) {
                return logicOperator;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
