package com.lc.mybatisx.parse;

public class SqlWhere {

    private String linkOp;

    private String field;

    private String op;

    public String getLinkOp() {
        return linkOp;
    }

    public void setLinkOp(String linkOp) {
        this.linkOp = linkOp;
    }

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
}
