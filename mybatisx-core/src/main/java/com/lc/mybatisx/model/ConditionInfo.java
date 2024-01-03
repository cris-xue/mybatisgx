package com.lc.mybatisx.model;

public class ConditionInfo {

    /**
     * AndUserNameEq
     */
    private String origin;
    /**
     * By、And、Or
     */
    private String linkOp;
    /**
     * user_name
     */
    private String dbColumnName;
    /**
     * =、<=、!=
     */
    private String op = "=";
    /**
     * [userName、Name]
     */
    private String javaColumnName;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getLinkOp() {
        return linkOp;
    }

    public void setLinkOp(String linkOp) {
        this.linkOp = linkOp;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public void setDbColumnName(String dbColumnName) {
        this.dbColumnName = dbColumnName;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getJavaColumnName() {
        return javaColumnName;
    }

    public void setJavaColumnName(String javaColumnName) {
        this.javaColumnName = javaColumnName;
    }
}
