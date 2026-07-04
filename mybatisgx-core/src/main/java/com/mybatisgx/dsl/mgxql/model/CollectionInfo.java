package com.mybatisgx.dsl.mgxql.model;

/**
 * 集合参数信息（IN / BETWEEN 操作使用）
 *
 * @author 薛承城
 * @date 2026/6/15
 */
public class CollectionInfo {

    /**
     * 集合元素类型
     */
    private Class<?> elementType;

    /**
     * foreach 变量名
     */
    private String itemName;

    public CollectionInfo() {
    }

    public CollectionInfo(Class<?> elementType, String itemName) {
        this.elementType = elementType;
        this.itemName = itemName;
    }

    public Class<?> getElementType() {
        return elementType;
    }

    public void setElementType(Class<?> elementType) {
        this.elementType = elementType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
