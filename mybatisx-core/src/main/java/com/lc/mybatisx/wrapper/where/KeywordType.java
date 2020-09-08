package com.lc.mybatisx.wrapper.where;

import java.util.Arrays;
import java.util.List;

public enum KeywordType {

    FIND(false, "find", Arrays.asList()),
    SELECT(false, "select", Arrays.asList()),
    TOP(true, "Top", Arrays.asList()),
    BY(false, "By", Arrays.asList()),
    GROUP_BY(true, "GroupBy", Arrays.asList()),
    SELECTIVE(false, "Selective", Arrays.asList()),
    AND(false, "And", Arrays.asList()),
    OR(true, "Or", Arrays.asList()),
    EQ(true, "Eq", Arrays.asList()),
    ORDER_BY(true, "OrderBy", Arrays.asList()),
    DESC(true, "Desc", Arrays.asList()),
    BETWEEN(true, "Between", Arrays.asList());

    private Boolean parse;
    private String keyword;
    private List<String> sql;

    KeywordType(Boolean parse, String keyword, List<String> sql) {
        this.parse = parse;
        this.keyword = keyword;
        this.sql = sql;
    }

    public Boolean getParse() {
        return parse;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<String> getDescription() {
        return sql;
    }

}
