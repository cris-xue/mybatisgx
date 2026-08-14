package com.mybatisgx.dsl.mgxql.model;

/**
 * 集合参数信息（IN / BETWEEN 操作使用）
 * <p>
 * 对齐 mgxsql {@code ForeachUnit}（design D6）：{@code itemName}/{@code collectionName}/{@code valueExpr} 三字段。
 * 简单 IN（{@code in (:ids)}）：itemName="item"、collectionName=参数名、valueExpr="#{item}"（itemName 与 valueExpr 由消费端默认）。
 * 复杂 IN（{@code in (item:coll)=>[item.id]}）：itemName/collectionName/valueExpr 三字段由文法显式提供，渲染为 mgxsql {@code in (item:coll)=>$item.valueExpr}（单字段，task 5.x）。
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
     * foreach 变量名（mgxsql <foreach item="...">，复杂 IN 由 (item:coll) 显式声明）
     */
    private String itemName;

    /**
     * 集合参数名（mgxsql <foreach collection="...">，简单 IN 为参数名，复杂 IN 为 (item:coll) 的 coll）
     */
    private String collectionName;

    /**
     * 迭代值表达式字段（复杂 IN 的 =>[field]，渲染为 mgxsql $item.field；单字段，task 5.x）
     */
    private String valueExpr;

    public CollectionInfo() {
    }

    public CollectionInfo(Class<?> elementType, String itemName) {
        this.elementType = elementType;
        this.itemName = itemName;
    }

    public CollectionInfo(Class<?> elementType, String itemName, String collectionName, String valueExpr) {
        this.elementType = elementType;
        this.itemName = itemName;
        this.collectionName = collectionName;
        this.valueExpr = valueExpr;
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

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getValueExpr() {
        return valueExpr;
    }

    public void setValueExpr(String valueExpr) {
        this.valueExpr = valueExpr;
    }
}

