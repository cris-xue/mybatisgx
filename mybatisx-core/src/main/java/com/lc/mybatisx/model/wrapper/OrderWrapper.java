package com.lc.mybatisx.model.wrapper;

import com.lc.mybatisx.parse.Keyword;

/**
 *
 */
public class OrderWrapper {

    private Keyword keyword = Keyword.ORDER_BY;

    private String sql;

    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
