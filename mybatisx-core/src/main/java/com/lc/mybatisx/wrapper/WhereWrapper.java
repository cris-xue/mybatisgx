package com.lc.mybatisx.wrapper;

import com.lc.mybatisx.wrapper.where.LinkOp;
import com.lc.mybatisx.wrapper.where.Operation;

/**
 * @author ：薛承城
 * @description：sql条件包装器
 * @date ：2020/7/5 15:54
 */
public class WhereWrapper {

    /**
     * 字段
     */
    private String field;

    /**
     * 操作符
     */
    private Operation operation;

    /**
     * 字段值
     */
    private String value;

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
    private WhereWrapper where;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public WhereWrapper getWhere() {
        return where;
    }

    public void setWhere(WhereWrapper where) {
        this.where = where;
    }

    /**
     * 连接基本表达式
     *
     * @param where
     * @param linkOp
     * @return
     */
    public void linkRule(WhereWrapper where, LinkOp linkOp) {
        this.setWhere(where);
        this.setLinkOp(linkOp);
    }

}
