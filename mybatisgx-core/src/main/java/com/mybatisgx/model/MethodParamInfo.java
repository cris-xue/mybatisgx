package com.mybatisgx.model;

import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
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
     * 类型分类
     */
    private TypeCategory typeCategory;
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
     * 参数名，单参数实体无注解参数为空
     */
    private String paramName;
    /**
     * 参数索引名称，如：arg0、arg1、arg2 。有@Param则使用@Param中的值
     */
    private String argName;
    /**
     * 方法参数或者方法参数实体，需要提前计算出公共取值路径，模板渲染的时候就不再需要多重逻辑判断
     */
    private List<String> argValueCommonPathItemList = new ArrayList<>();
    /**
     * 容器类型，List
     */
    private Class<?> collectionType;
    /**
     * Collection类型名
     */
    private String collectionTypeName;
    /**
     * 实体参数对应的实体信息
     */
    private EntityInfo entityInfo;
    /**
     * 字段信息列表。如果是普通对象参数，不为空，比如：ID复合字段。如果是实体参数，为空。
     * 主要解决复合字段子字段无法存储的问题，场景1：findById(MultiId)。场景2：findList(MultiIdUser)
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
    /**
     * 是否被包装过，mybatis只有单参数实体无注解参数才不会包装
     */
    private Boolean isWrapper = false;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public TypeCategory getClassCategory() {
        return typeCategory;
    }

    public void setClassCategory(TypeCategory typeCategory) {
        this.typeCategory = typeCategory;
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

    public List<String> getArgValueCommonPathItemList() {
        return argValueCommonPathItemList;
    }

    public void setArgValueCommonPathItemList(List<String> argValueCommonPathItemList) {
        this.argValueCommonPathItemList = argValueCommonPathItemList;
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

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
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

    public Boolean getWrapper() {
        return isWrapper;
    }

    public void setWrapper(Boolean wrapper) {
        isWrapper = wrapper;
    }
}
