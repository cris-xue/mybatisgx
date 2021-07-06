package com.lc.mybatisx.keyword;

public class WhereKeyword {

    private String linkOp;

    private String field;

    private String op;

    private String leftParentheses;

    private String rightParentheses;

    private WhereKeyword whereKeyword;

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

    public String getLeftParentheses() {
        return leftParentheses;
    }

    public void setLeftParentheses(String leftParentheses) {
        this.leftParentheses = leftParentheses;
    }

    public String getRightParentheses() {
        return rightParentheses;
    }

    public void setRightParentheses(String rightParentheses) {
        this.rightParentheses = rightParentheses;
    }

    public WhereKeyword getWhereKeyword() {
        return whereKeyword;
    }

    public void setWhereKeyword(WhereKeyword whereKeyword) {
        this.whereKeyword = whereKeyword;
    }
}
