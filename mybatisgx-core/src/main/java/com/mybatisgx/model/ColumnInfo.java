package com.mybatisgx.model;

import com.mybatisgx.annotation.*;
import com.mybatisgx.api.GeneratedValueHandler;

import java.util.List;

/**
 * 字段信息信息
 *
 * @author ccxuef
 * @date 2025/8/9 15:54
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
     * 数据库类型名
     */
    private String dbTypeName;
    /**
     * 数据库列名
     */
    private String dbColumnName;
    /**
     * 数据库列名别名，用于查询
     */
    private String dbColumnNameAlias;
    /**
     * 类型处理器
     */
    private String typeHandler;
    /**
     * 复合字段列表
     */
    private List<ColumnInfo> composites;
    /**
     * 字段值生成处理器
     */
    private GeneratedValueHandler generatedValueHandler;
    /**
     * 字段注解
     */
    private Column column;
    /**
     * 非持久化字段
     */
    private Transient nonPersistent;
    /**
     * 是否是乐观锁字段
     */
    private Lock lock;
    /**
     * 是否是逻辑删除字段
     */
    private LogicDelete logicDelete;
    /**
     * 生成值注解
     */
    private GeneratedValue generatedValue;

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
        if (javaType != null) {
            this.javaTypeName = javaType.getTypeName();
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

    public Class<?> getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(Class<?> collectionType) {
        this.collectionType = collectionType;
        if (collectionType != null) {
            this.collectionTypeName = collectionType.getName();
        }
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

    public String getDbColumnNameAlias() {
        return dbColumnNameAlias;
    }

    public String getTableColumnNameAlias(ColumnEntityRelation columnEntityRelation) {
        return String.format("%s_%s", columnEntityRelation.getTableNameAlias(), dbColumnNameAlias);
    }

    public void setDbColumnNameAlias(String dbColumnNameAlias) {
        this.dbColumnNameAlias = dbColumnNameAlias;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public List<ColumnInfo> getComposites() {
        return composites;
    }

    public void setComposites(List<ColumnInfo> composites) {
        this.composites = composites;
    }

    public GeneratedValueHandler getGenerateValueHandler() {
        return generatedValueHandler;
    }

    public void setGenerateValueHandler(GeneratedValueHandler generatedValueHandler) {
        this.generatedValueHandler = generatedValueHandler;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public Transient getNonPersistent() {
        return nonPersistent;
    }

    public void setNonPersistent(Transient nonPersistent) {
        this.nonPersistent = nonPersistent;
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

    public GeneratedValue getGenerateValue() {
        return generatedValue;
    }

    public void setGenerateValue(GeneratedValue generatedValue) {
        this.generatedValue = generatedValue;
    }

    public static class Builder {

        private ColumnInfo columnInfo = new ColumnInfo();

        public Builder columnInfo(ColumnInfo columnInfo) {
            this.columnInfo.setJavaTypeName(columnInfo.getJavaTypeName());
            this.columnInfo.setDbColumnName(columnInfo.getDbColumnName());
            this.columnInfo.setDbColumnNameAlias(columnInfo.getDbColumnNameAlias());
            return this;
        }

        public Builder javaColumnName(String javaColumnName) {
            this.columnInfo.setJavaColumnName(javaColumnName);
            return this;
        }

        public ColumnInfo build() {
            return columnInfo;
        }
    }
}
