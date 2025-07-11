package com.lc.mybatisx.model;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:13
 */
public class MethodParamInfo {

    /**
     * 参数索引
     */
    private Integer index;
    /**
     * 是否基础类型
     */
    private Boolean isBasicType;
    /**
     * 类型，Integer、User、Map
     */
    private Class<?> type;
    /**
     * 类型名称：java.lang.Long、java.lang.Object、java.util.Map
     */
    private String typeName;
    /**
     * 参数名，userName。有@Param则使用@Param中的值，如果没有则使用param1、param2这种方式
     */
    private String paramName;
    /**
     * 参数索引名称，param1、param2
     */
    private String argName;
    /**
     * 是否Collection类型
     */
    private Boolean isCollectionType = false;
    /**
     * 字段信息
     */
    private List<ColumnInfo> columnInfoList;
    /**
     * 是否容器类型
     */
    private Boolean isContainerType = false;
    /**
     * 容器类型，List、Map
     */
    private Class<?> containerType;
    /**
     * 容器类型名
     */
    private String containerTypeName;
    /**
     * 是否批量参数
     */
    private Boolean isBatchSize = false;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

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

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getArgName() {
        return argName;
    }

    public void setArgName(String argName) {
        this.argName = argName;
    }

    public Boolean getCollectionType() {
        return isCollectionType;
    }

    public void setCollectionType(Boolean collectionType) {
        isCollectionType = collectionType;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }

    public Boolean getContainerType() {
        return isContainerType;
    }

    public void setContainerType(Class<?> containerType) {
        this.containerType = containerType;
    }

    public String getContainerTypeName() {
        return containerTypeName;
    }

    public void setContainerTypeName(String containerTypeName) {
        this.containerTypeName = containerTypeName;
    }

    public Boolean getBatchSize() {
        return isBatchSize;
    }

    public void setBatchSize(Boolean batchSize) {
        isBatchSize = batchSize;
    }

    public void setContainerType(Boolean containerType) {
        isContainerType = containerType;
    }
}
