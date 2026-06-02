package com.mybatisgx.dsl.mgxql.model;

/**
 * MGXQL ORDER BY项模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class OrderByItem {

    /**
     * 排序字段引用
     */
    private FieldReference field;

    /**
     * 排序方向：asc（默认）或 desc
     */
    private String direction = "asc";

    public OrderByItem() {
    }

    public OrderByItem(FieldReference field, String direction) {
        this.field = field;
        this.direction = direction;
    }

    public FieldReference getField() {
        return field;
    }

    public void setField(FieldReference field) {
        this.field = field;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
