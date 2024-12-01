package com.lc.mybatisx.model;

import javax.persistence.FetchType;

public class ManyToManyInfo {

    private String javaColumnName;
    /**
     * 关联类型，多对多，一对多，一对一
     */
    private String type;

    public Class<?> targetEntity;

    public Class<?> associationEntity;
    /**
     * 外键字段
     *
     * @return
     */
    String[] foreignKey;

    String[] inverseForeignKey;

    FetchType fetch;

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

    public Class<?> getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(Class<?> targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Class<?> getAssociationEntity() {
        return associationEntity;
    }

    public void setAssociationEntity(Class<?> associationEntity) {
        this.associationEntity = associationEntity;
    }

    public String[] getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String[] foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String[] getInverseForeignKey() {
        return inverseForeignKey;
    }

    public void setInverseForeignKey(String[] inverseForeignKey) {
        this.inverseForeignKey = inverseForeignKey;
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
