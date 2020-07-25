package com.lc.mybatisx.wrapper.where;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/2/21 17:23
 */
public enum Operation {

    EQ("=", "等于"),
    LT("<", "小于"),
    GT(">", "大于"),
    GTEQ(">=", "大于等于"),
    LTEQ("<=", "小于等于"),
    NOTEQ("<>", "不等"),
    IN("in", "");

    private String key;
    private String value;

    Operation(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

}
