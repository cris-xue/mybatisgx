package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.*;
import com.lc.mybatisx.annotation.handler.GenerateValueHandler;

/**
 * 列信息
 */
public class ColumnInfo {

    /**
     * Map、基础类型、业务类型
     */
    private Class<?> javaType;
    /**
     *
     */
    private String javaTypeName;
    /**
     *
     */
    private String javaColumnName;
    /**
     * 容器类型，List、Set
     */
    private Class<?> collectionType;
    /**
     * 容器类型名
     */
    private String collectionTypeName;
    /**
     *
     */
    private String dbTypeName;
    /**
     *
     */
    private String dbColumnName;
    /**
     * 类型处理器
     */
    private String typeHandler;
    /**
     * 是否是主键
     */
    private Id id;
    /**
     * 是否是乐观锁字段
     */
    private Lock lock;
    /**
     * 是否是逻辑删除字段
     */
    private LogicDelete logicDelete;
    /**
     * 是否是租户字段
     */
    private TenantId tenantId;
    /**
     * 字段注解信息 TODO 字段所有的注解信息需要放在注解中
     */
    private ColumnInfoAnnotationInfo columnInfoAnnotationInfo;
    /**
     * 生成值注解
     */
    private GenerateValue generateValue;
    /**
     * 字段值生成处理器
     */
    private GenerateValueHandler generateValueHandler;

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
        this.javaTypeName = javaType.getTypeName();
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public void setJavaTypeName(String javaTypeName) {
        this.javaTypeName = javaTypeName;
    }

    public String getJavaColumnName() {
        return javaColumnName;
    }

    public void setJavaColumnName(String javaColumnName) {
        this.javaColumnName = javaColumnName;
    }

    public Class<?> getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(Class<?> collectionType) {
        this.collectionType = collectionType;
        this.collectionTypeName = collectionType.getName();
    }

    public String getCollectionTypeName() {
        return collectionTypeName;
    }

    public void setCollectionTypeName(String collectionTypeName) {
        this.collectionTypeName = collectionTypeName;
    }

    public String getDbTypeName() {
        return dbTypeName;
    }

    public void setDbTypeName(String dbTypeName) {
        this.dbTypeName = dbTypeName;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public void setDbColumnName(String dbColumnName) {
        this.dbColumnName = dbColumnName;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public LogicDelete getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(LogicDelete logicDelete) {
        this.logicDelete = logicDelete;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public ColumnInfoAnnotationInfo getColumnInfoAnnotationInfo() {
        return columnInfoAnnotationInfo;
    }

    public void setColumnInfoAnnotationInfo(ColumnInfoAnnotationInfo columnInfoAnnotationInfo) {
        this.columnInfoAnnotationInfo = columnInfoAnnotationInfo;
    }

    public GenerateValue getGenerateValue() {
        return generateValue;
    }

    public void setGenerateValue(GenerateValue generateValue) {
        this.generateValue = generateValue;
    }

    public GenerateValueHandler getGenerateValueHandler() {
        return generateValueHandler;
    }

    public void setGenerateValueHandler(GenerateValueHandler generateValueHandler) {
        this.generateValueHandler = generateValueHandler;
    }
}
