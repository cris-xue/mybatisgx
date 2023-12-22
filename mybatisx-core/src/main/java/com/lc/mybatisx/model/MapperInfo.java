package com.lc.mybatisx.model;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:13
 */
public class MapperInfo {

    private Class<?> idClass;

    private Class<?> entityClass;
    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 表信息
     */
    private TableInfo tableInfo;
    /**
     * 字段信息列表
     */
    private List<ColumnInfo> columnInfoList;
    /**
     * 方法信息列表
     */
    private List<MethodInfo> methodInfoList;
    /**
     * 结果集信息
     */
    private ResultMapInfo resultMapInfo;

    public Class<?> getIdClass() {
        return idClass;
    }

    public void setIdClass(Class<?> idClass) {
        this.idClass = idClass;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }

    public List<MethodInfo> getMethodInfoList() {
        return methodInfoList;
    }

    public void setMethodInfoList(List<MethodInfo> methodInfoList) {
        this.methodInfoList = methodInfoList;
    }

    public ResultMapInfo getResultMapInfo() {
        return resultMapInfo;
    }

    public void setResultMapInfo(ResultMapInfo resultMapInfo) {
        this.resultMapInfo = resultMapInfo;
    }
}
