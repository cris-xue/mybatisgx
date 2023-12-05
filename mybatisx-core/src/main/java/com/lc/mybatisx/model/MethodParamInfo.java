package com.lc.mybatisx.model;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:13
 */
public class MethodParamInfo {

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
     * 参数名，userName
     */
    private String paramName;
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

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }

    public Boolean getIsContainerType() {
        return this.isContainerType;
    }

    public void setIsContainerType(Boolean isContainerType) {
        this.isContainerType = isContainerType;
    }

    public Class<?> getContainerType() {
        return this.containerType;
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

}
