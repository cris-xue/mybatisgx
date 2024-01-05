package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;

/**
 * 列信息
 */
public class ColumnInfo {

    private Class<?> javaType;

    private String javaTypeName;

    private String javaColumnName;

    private String dbTypeName;

    private String dbColumnName;
    /**
     * 是否是主键
     */
    private YesOrNo primaryKey = YesOrNo.NO;
    /**
     * 类型处理器
     */
    private String typeHandler;
    /**
     * 是否是乐观锁字段
     */
    private Lock lock;
    /**
     * 是否是逻辑删除字段
     */
    private LogicDelete delete;

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
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

    public YesOrNo getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(YesOrNo primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public LogicDelete getDelete() {
        return delete;
    }

    public void setDelete(LogicDelete delete) {
        this.delete = delete;
    }
}
