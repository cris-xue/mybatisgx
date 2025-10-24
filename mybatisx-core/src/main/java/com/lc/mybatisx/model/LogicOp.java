package com.lc.mybatisx.model;

public enum LogicOp {

    BY("By", "", "等值查询"),
    AND("And", "And", "等值查询"),
    OR("Or", "Or", "等值查询");

    private String key;
    private String logicOp;
    private String logicOpDesc;

    LogicOp(String key, String logicOp, String logicOpDesc) {
        this.key = key;
        this.logicOp = logicOp;
        this.logicOpDesc = logicOpDesc;
    }

    public static LogicOp getLogicOp(String key) {
        for (LogicOp logicOp : LogicOp.values()) {
            if (logicOp.key.equals(key)) {
                return logicOp;
            }
        }
        return null;
    }
}
