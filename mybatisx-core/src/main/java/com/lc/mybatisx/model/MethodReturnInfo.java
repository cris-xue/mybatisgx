package com.lc.mybatisx.model;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:14
 */
public class MethodReturnInfo {

    /**
     * 是否基础类型
     */
    private Boolean isBasicType;
    /**
     * 类型，Integer、User
     */
    private Class<?> type;
    /**
     * 类型名称：java.lang.Long、java.lang.Object
     */
    private String typeName;
    /**
     * 是否Collection类型
     */
    private Boolean isCollectionType = false;
    /**
     * 容器类型，List
     */
    private Class<?> collectionType;
    /**
     * Collection类型名
     */
    private String collectionTypeName;
    /**
     * 字段信息
     */
    List<ColumnInfo> columnInfoList;

    public Boolean getBasicType() {
        return isBasicType;
    }

    public void setBasicType(Boolean basicType) {
        isBasicType = basicType;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Boolean getCollectionType() {
        return isCollectionType;
    }

    public void setCollectionType(Class<?> collectionType) {
        this.collectionType = collectionType;
    }

    public String getCollectionTypeName() {
        return collectionTypeName;
    }

    public void setCollectionTypeName(String collectionTypeName) {
        this.collectionTypeName = collectionTypeName;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }

    public void setCollectionType(Boolean collectionType) {
        isCollectionType = collectionType;
    }
}
