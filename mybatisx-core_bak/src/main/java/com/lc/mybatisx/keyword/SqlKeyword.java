package com.lc.mybatisx.keyword;

public class SqlKeyword {

    private String action;

    private WhereKeyword whereKeyword;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public WhereKeyword getWhereKeyword() {
        return whereKeyword;
    }

    public void setWhereKeyword(WhereKeyword whereKeyword) {
        this.whereKeyword = whereKeyword;
    }
}
