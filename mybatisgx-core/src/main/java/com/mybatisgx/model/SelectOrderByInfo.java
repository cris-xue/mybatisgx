package com.mybatisgx.model;

/**
 * 查询排序信息
 * @author 薛承城
 * @date 2025/11/20 12:22
 */
public class SelectOrderByInfo {

    /**
     * 排序字段
     */
    private String column;
    /**
     * 排序方向【asc、desc】
     */
    private String direction;

    public SelectOrderByInfo(String column, String direction) {
        this.column = column;
        this.direction = direction;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
