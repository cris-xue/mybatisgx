package com.lc.mybatisx.wrapper.where;

import java.util.Arrays;
import java.util.List;

public enum Keyword {

    INSERT(false, Arrays.asList(), ""),
    DELETE(false, Arrays.asList(), ""),
    UPDATE(false, Arrays.asList(), ""),
    SELECT(false, Arrays.asList(), ""),
    ;

    private Boolean parse;
    private List<String> keyword;
    private String description;

    Keyword(Boolean parse, List<String> keyword, String description) {
        this.parse = parse;
        this.keyword = keyword;
        this.description = description;
    }

    public Boolean getParse() {
        return parse;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public String getDescription() {
        return description;
    }

}
