package com.mybatisgx.model;

public enum LogicOperator {

    AND("And", "and"),
    OR("Or", "or"),
    NULL("Null", "");

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
        return LogicOperator.NULL;
    }

    public String getValue() {
        return value;
    }
}
