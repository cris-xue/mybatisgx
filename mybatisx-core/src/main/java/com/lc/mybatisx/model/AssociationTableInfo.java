package com.lc.mybatisx.model;

import javax.persistence.FetchType;

public class AssociationTableInfo {

    /**
     * 关联查询映射的java字段
     */
    private String javaColumnName;
    /**
     * 关联类型，多对多，一对多，一对一
     */
    private String type;
    /**
     * 关联表实体，如UserRole、RoleResource
     */
    private Class<?> associationEntityClass;
    /**
     * 目标表实体类名
     */
    private String associationEntityClassName;
    /**
     * 关联表表名，如user_role
     */
    private String associationTableName;
    /**
     * 当前表在关联表中的外键字段
     */
    private String[] foreignKey;
    /**
     * 目标表实体
     */
    private Class<?> targetEntityClass;
    /**
     * 目标表实体类名
     */
    private String targetEntityClassName;
    /**
     * 目标表表名
     */
    private String targetTableName;
    /**
     * 目标表在关联表中的外键字段
     */
    private String[] targetForeignKey;

    private FetchType fetch = FetchType.LAZY;

    private String select;

    public String getJavaColumnName() {
        return javaColumnName;
    }

    public void setJavaColumnName(String javaColumnName) {
        this.javaColumnName = javaColumnName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Class<?> getAssociationEntityClass() {
        return associationEntityClass;
    }

    public void setAssociationEntityClass(Class<?> associationEntityClass) {
        this.associationEntityClass = associationEntityClass;
    }

    public String getAssociationEntityClassName() {
        return associationEntityClassName;
    }

    public void setAssociationEntityClassName(String associationEntityClassName) {
        this.associationEntityClassName = associationEntityClassName;
    }

    public String getAssociationTableName() {
        return associationTableName;
    }

    public void setAssociationTableName(String associationTableName) {
        this.associationTableName = associationTableName;
    }

    public String[] getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String[] foreignKey) {
        this.foreignKey = foreignKey;
    }

    public Class<?> getTargetEntityClass() {
        return targetEntityClass;
    }

    public void setTargetEntityClass(Class<?> targetEntityClass) {
        this.targetEntityClass = targetEntityClass;
        this.targetEntityClassName = targetEntityClass.getName();
    }

    public String getTargetEntityClassName() {
        return targetEntityClassName;
    }

    public void setTargetEntityClassName(String targetEntityClassName) {
        this.targetEntityClassName = targetEntityClassName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String[] getTargetForeignKey() {
        return targetForeignKey;
    }

    public void setTargetForeignKey(String[] targetForeignKey) {
        this.targetForeignKey = targetForeignKey;
    }

    public FetchType getFetch() {
        return fetch;
    }

    public void setFetch(FetchType fetch) {
        this.fetch = fetch;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }
}
