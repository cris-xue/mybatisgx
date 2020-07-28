package com.lc.mybatisx.wrapper.where;

import java.util.Arrays;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/2/21 17:23
 */
public enum Operation {

    LT("<", Arrays.asList("LessThan", "Lt"), "小于"),
    LTEQ("<=", Arrays.asList("LessThanEquals", "Lteq"), "小于等于"),
    GT(">", Arrays.asList("GreaterThan", "Gt"), "大于"),
    GTEQ(">=", Arrays.asList("GreaterThanEquals", "Gteq"), "大于等于"),
    NOTEQ("<>", Arrays.asList("Not"), "不等"),
    IN("in", Arrays.asList("In"), "包含"),
    IS("=", Arrays.asList("Is"), "等于"),
    EQ("=", Arrays.asList("Equals", "Eq", ""), "等于");

    private String key;
    private List<String> name;
    private String description;

    Operation(String key, List<String> name, String description) {
        this.key = key;
        this.name = name;
        this.description = description;
    }

    public String getKey() {
        return this.key;
    }

    public List<String> getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
