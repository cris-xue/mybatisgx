package com.lc.mybatisx.wrapper;

import com.lc.mybatisx.wrapper.where.LinkOp;
import com.lc.mybatisx.wrapper.where.Operation;

import java.util.List;

/**
 * @author ：薛承城
 * @description：sql条件包装器
 * @date ：2020/7/5 15:54
 */
public class WhereWrapper {

    /**
     * 数据库字段
     */
    private String dbColumn;

    private String dbType;

    /**
     * 操作符
     */
    private Operation operation;

    /**
     * java字段【可以满足between】
     */
    private List<String> javaColumn;

    private String javaType;

    /**
     * 两个条件间的连接符【and,or】
     */
    private LinkOp linkOp;

    /**
     * 左括号
     */
    private String leftBracket;

    /**
     * 右括号
     */
    private String rightBracket;

    /**
     * 连接条件
     */
    private WhereWrapper whereWrapper;

    public String getDbColumn() {
        return dbColumn;
    }

    public void setDbColumn(String dbColumn) {
        this.dbColumn = dbColumn;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public List<String> getJavaColumn() {
        return javaColumn;
    }

    public void setJavaColumn(List<String> javaColumn) {
        this.javaColumn = javaColumn;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public LinkOp getLinkOp() {
        return linkOp;
    }

    public void setLinkOp(LinkOp linkOp) {
        this.linkOp = linkOp;
    }

    public String getLeftBracket() {
        return leftBracket;
    }

    public void setLeftBracket(String leftBracket) {
        this.leftBracket = leftBracket;
    }

    public String getRightBracket() {
        return rightBracket;
    }

    public void setRightBracket(String rightBracket) {
        this.rightBracket = rightBracket;
    }

    public WhereWrapper getWhereWrapper() {
        return whereWrapper;
    }

    public void setWhereWrapper(WhereWrapper whereWrapper) {
        this.whereWrapper = whereWrapper;
    }

    /**
     * 连接基本表达式
     *
     * @param whereWrapper
     * @param linkOp
     * @return
     */
    public void linkRule(WhereWrapper whereWrapper, LinkOp linkOp) {
        this.setWhereWrapper(whereWrapper);
        this.setLinkOp(linkOp);
    }

}
