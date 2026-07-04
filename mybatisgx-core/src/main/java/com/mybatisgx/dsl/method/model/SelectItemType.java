package com.mybatisgx.dsl.method.model;

/**
 * sql方法类型
 * @author 薛承城
 * @date 2025/11/20 18:12
 */
public enum SelectItemType {

    COLUMN("column", "*"),
    COUNT("count", "count(*)");

    private String key;
    private String value;

    SelectItemType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
