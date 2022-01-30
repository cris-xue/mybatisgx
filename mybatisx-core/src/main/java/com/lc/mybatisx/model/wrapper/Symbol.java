package com.lc.mybatisx.model.wrapper;

import java.util.Arrays;
import java.util.List;

/**
 * @author ：薛承城
 * @description：关键字符号
 * @date ：2020/2/21 17:23
 */
public enum Symbol {

    LT("<![CDATA[ < ]]>", Arrays.asList("LessThan", "Lt"), "小于"),
    LTEQ("<![CDATA[ <= ]]>", Arrays.asList("LessThanEquals", "Lteq"), "小于等于"),
    GT("<![CDATA[ > ]]>", Arrays.asList("GreaterThan", "Gt"), "大于"),
    GTEQ("<![CDATA[ >= ]]>", Arrays.asList("GreaterThanEquals", "Gteq"), "大于等于"),
    IN("in", Arrays.asList("In"), "包含"),
    IS("=", Arrays.asList("Is"), "等于"),
    EQ("=", Arrays.asList("Equals", "Eq", ""), "等于"),
    NOT("<![CDATA[ <> ]]>", Arrays.asList("Not"), "不等"),
    NOTEQ("<![CDATA[ <> ]]>", Arrays.asList("Not"), "不等"),
    BETWEEN("", Arrays.asList(), "");

    private String key;
    private List<String> name;
    private String description;

    Symbol(String key, List<String> name, String description) {
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
