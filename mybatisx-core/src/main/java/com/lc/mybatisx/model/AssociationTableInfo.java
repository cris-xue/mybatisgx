package com.lc.mybatisx.model;

import javax.persistence.FetchType;

/**
 * @author ：薛承城
 * @description：关联表信息，多对多中间关联表没有太大的以意义，完全是为了关系型数据库考虑。但有时候需要处理关联问题的开关，又有一定的作用，所以需要当作正常表来处理
 * @date ：2021/7/9 17:14
 */
public class AssociationTableInfo {

    /**
     * 关联查询映射的java字段
     */
    private String javaColumnName;
    /**
     * 关系维护实体类型，外键在那个实体，那个实体就是关系维护实体
     */
    private Class<?> junctionEntity;
    /**
     * join的实体
     */
    private Class<?> joinEntity;
    /**
     * join容器类型
     */
    private Class<?> joinContainerType;
    /**
     * 外键字段
     *
     * @return
     */
    private String foreignKey;
    /**
     * 另一张表外键字段
     *
     * @return
     */
    private String inverseForeignKey;
    /**
     * 抓取策略
     */
    private FetchType fetch = FetchType.LAZY;

    public String getJavaColumnName() {
        return javaColumnName;
    }

    public void setJavaColumnName(String javaColumnName) {
        this.javaColumnName = javaColumnName;
    }

    public Class<?> getJunctionEntity() {
        return junctionEntity;
    }

    public void setJunctionEntity(Class<?> junctionEntity) {
        this.junctionEntity = junctionEntity;
    }

    public Class<?> getJoinEntity() {
        return joinEntity;
    }

    public void setJoinEntity(Class<?> joinEntity) {
        this.joinEntity = joinEntity;
    }

    public Class<?> getJoinContainerType() {
        return joinContainerType;
    }

    public void setJoinContainerType(Class<?> joinContainerType) {
        this.joinContainerType = joinContainerType;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getInverseForeignKey() {
        return inverseForeignKey;
    }

    public void setInverseForeignKey(String inverseForeignKey) {
        this.inverseForeignKey = inverseForeignKey;
    }

    public FetchType getFetch() {
        return fetch;
    }

    public void setFetch(FetchType fetch) {
        this.fetch = fetch;
    }
}
