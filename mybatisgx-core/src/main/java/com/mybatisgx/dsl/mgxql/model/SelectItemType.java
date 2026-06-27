package com.mybatisgx.dsl.mgxql.model;

/**
 * MGXQL查询项类型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public enum SelectItemType {

    /**
     * select * 或 entity.*
     */
    COLUMN_ALL("COUNT", "*"),
    /**
     * 自定义列：select id, name 或 entity.name
     */
    COLUMN("COUNT", ""),
    /**
     * count聚合函数
     */
    COUNT("COUNT", "COUNT"),
    /**
     * max聚合函数
     */
    MAX("MAX", "MAX"),
    /**
     * min聚合函数
     */
    MIN("MIN", "MIN"),
    /**
     * avg聚合函数
     */
    AVG("AVG", "AVG"),
    /**
     * sum聚合函数
     */
    SUM("SUM", "SUM");

    private String type;
    private String value;

    SelectItemType(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean hasAggregateFunction() {
        if (this.name().equals(SelectItemType.COLUMN_ALL.name()) || this.name().equals(SelectItemType.COLUMN.name())) {
            return false;
        }
        return true;
    }
}
