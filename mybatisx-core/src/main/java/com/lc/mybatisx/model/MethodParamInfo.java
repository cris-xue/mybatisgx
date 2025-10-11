package com.lc.mybatisx.model;

import org.apache.ibatis.annotations.Param;

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
     * 参数注解
     */
    private Param param;
    /**
     * 参数名，如：param1、param2、param3
     */
    private String paramName;
    /**
     * 参数索引名称，如：arg0、arg1、arg2 。有@Param则使用@Param中的值
     */
    private String argName;
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
    private List<ColumnInfo> columnInfoList;
    /**
     * 是否批量参数
     */
    private Boolean isBatchSize = false;
    /**
     * 是否批量数据
     */
    private Boolean isBatchData = false;
    /**
     * 批量参数名称
     */
    private String batchItemName;

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

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
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

    public Class<?> getCollectionType() {
        return collectionType;
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

    public Boolean getBatchSize() {
        return isBatchSize;
    }

    public void setBatchSize(Boolean batchSize) {
        isBatchSize = batchSize;
    }

    public Boolean getBatchData() {
        return isBatchData;
    }

    public void setBatchData(Boolean batchData) {
        isBatchData = batchData;
    }

    public String getBatchItemName() {
        return batchItemName;
    }

    public void setBatchItemName(String batchItemName) {
        this.batchItemName = batchItemName;
    }
}
