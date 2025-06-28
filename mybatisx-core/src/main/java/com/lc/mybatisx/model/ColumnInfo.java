package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.*;
import com.lc.mybatisx.annotation.handler.GenerateValueHandler;

import java.lang.reflect.Type;

/**
 * 列信息
 */
public class ColumnInfo {

    /**
     *
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
    private Class<?> containerType;
    /**
     * 容器类型名
     */
    private String containerTypeName;
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
     * 关联信息
     */
    private AssociationEntityInfo associationEntityInfo;
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

    public void setJavaType(Type javaType) {
        if (javaType instanceof Class) {
            this.javaType = (Class<?>) javaType;
        }
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

    public Class<?> getContainerType() {
        return containerType;
    }

    public void setContainerType(Class<?> containerType) {
        this.containerType = containerType;
        this.containerTypeName = containerType.getName();
    }

    public String getContainerTypeName() {
        return containerTypeName;
    }

    public void setContainerTypeName(String containerTypeName) {
        this.containerTypeName = containerTypeName;
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

    public AssociationEntityInfo getAssociationEntityInfo() {
        return associationEntityInfo;
    }

    public void setAssociationEntityInfo(AssociationEntityInfo associationEntityInfo) {
        this.associationEntityInfo = associationEntityInfo;
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
