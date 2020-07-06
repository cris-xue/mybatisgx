package com.lc.mybatisx.wrapper;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/6 15:54
 */
public class WhereWrapper {

    private String field;

    private String op;

    private String value;

    private String linkOp;

    private WhereWrapper whereWrapper;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLinkOp() {
        return linkOp;
    }

    public void setLinkOp(String linkOp) {
        this.linkOp = linkOp;
    }

    public WhereWrapper getWhereWrapper() {
        return whereWrapper;
    }

    public void setWhereWrapper(WhereWrapper whereWrapper) {
        this.whereWrapper = whereWrapper;
    }

}
