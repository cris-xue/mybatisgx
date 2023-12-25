package com.lc.mybatisx.model;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:13
 */
public class MapperInfo {

    /**
     * id类型
     */
    private Class<?> idClass;
    /**
     * 实体类型
     */
    private Class<?> entityClass;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 命名空间
     */
    private String namespace;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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
