package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

public class ConditionInfo {

    /**
     * 查询条件在方法名中的位置，如findById、findByName，其实位置从0开始
     */
    private Integer index;
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
    /**
     * 条件对应的方法中的参数
     */
    private List<String> paramName = new ArrayList<>();

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

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

    public List<String> getParamName() {
        return paramName;
    }

    public void addParamName(String paramName) {
        this.paramName.add(paramName);
    }

    public void setParamName(List<String> paramName) {
        this.paramName = paramName;
    }
}
