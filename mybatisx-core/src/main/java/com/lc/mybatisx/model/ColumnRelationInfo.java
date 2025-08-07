package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 字段关系信息
 *
 * @author ccxuef
 * @date 2025/7/27 13:20
 */
public class ColumnRelationInfo {

    /**
     * 关系维护放
     */
    private String mappedBy;
    /**
     * 获取方式
     */
    private String fetch;
    /**
     * 数据抓取大小
     */
    private String fetchSize;
    /**
     * 加载策略
     */
    private LoadStrategy loadStrategy;
    /**
     * 当前实体外键字段列表
     */
    private List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = new ArrayList();
    /**
     * 关联实体外键字段列表
     */
    private List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = new ArrayList();
    /**
     * 一对一
     */
    private OneToOne oneToOne;
    /**
     * 一对多
     */
    private OneToMany oneToMany;
    /**
     * 多对一
     */
    private ManyToOne manyToOne;
    /**
     * 多对多
     */
    private ManyToMany manyToMany;
    /**
     * 关联字段
     */
    private JoinColumn joinColumn;
    /**
     * 多关联字段
     */
    private JoinColumns joinColumns;
    /**
     * 关联表
     */
    private JoinTable joinTable;

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    public String getFetch() {
        return fetch;
    }

    public void setFetch(String fetch) {
        this.fetch = fetch;
    }

    public String getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(String fetchSize) {
        this.fetchSize = fetchSize;
    }

    public LoadStrategy getLoadStrategy() {
        return loadStrategy;
    }

    public void setLoadStrategy(LoadStrategy loadStrategy) {
        this.loadStrategy = loadStrategy;
    }

    public List<ForeignKeyColumnInfo> getForeignKeyColumnInfoList() {
        return foreignKeyColumnInfoList;
    }

    public void setForeignKeyColumnInfoList(List<ForeignKeyColumnInfo> foreignKeyColumnInfoList) {
        this.foreignKeyColumnInfoList = foreignKeyColumnInfoList;
    }

    public List<ForeignKeyColumnInfo> getInverseForeignKeyColumnInfoList() {
        return inverseForeignKeyColumnInfoList;
    }

    public void setInverseForeignKeyColumnInfoList(List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList) {
        this.inverseForeignKeyColumnInfoList = inverseForeignKeyColumnInfoList;
    }

    public OneToOne getOneToOne() {
        return oneToOne;
    }

    public void setOneToOne(OneToOne oneToOne) {
        this.oneToOne = oneToOne;
        if (oneToOne != null) {
            this.mappedBy = oneToOne.mappedBy();
            this.fetch = oneToOne.fetch().name();
            this.fetchSize = oneToOne.fetchSize() == 0 ? null : Integer.valueOf(oneToOne.fetchSize()).toString();
            this.loadStrategy = oneToOne.loadStrategy();
        }
    }

    public OneToMany getOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(OneToMany oneToMany) {
        this.oneToMany = oneToMany;
        if (oneToMany != null) {
            this.mappedBy = oneToMany.mappedBy();
            this.fetch = oneToMany.fetch().name();
            this.fetchSize = oneToMany.fetchSize() == 0 ? null : Integer.valueOf(oneToMany.fetchSize()).toString();
            this.loadStrategy = oneToMany.loadStrategy();
        }
    }

    public ManyToOne getManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(ManyToOne manyToOne) {
        this.manyToOne = manyToOne;
        if (manyToOne != null) {
            this.mappedBy = manyToOne.mappedBy();
            this.fetch = manyToOne.fetch().name();
            this.fetchSize = manyToOne.fetchSize() == 0 ? null : Integer.valueOf(manyToOne.fetchSize()).toString();
            this.loadStrategy = manyToOne.loadStrategy();
        }
    }

    public ManyToMany getManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(ManyToMany manyToMany) {
        this.manyToMany = manyToMany;
        if (manyToMany != null) {
            this.mappedBy = manyToMany.mappedBy();
            this.fetch = manyToMany.fetch().name();
            this.fetchSize = manyToMany.fetchSize() == 0 ? null : Integer.valueOf(manyToMany.fetchSize()).toString();
            this.loadStrategy = manyToMany.loadStrategy();
        }
    }

    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public void setJoinColumn(JoinColumn joinColumn) {
        this.joinColumn = joinColumn;
    }

    public JoinColumns getJoinColumns() {
        return joinColumns;
    }

    public void setJoinColumns(JoinColumns joinColumns) {
        this.joinColumns = joinColumns;
    }

    public JoinTable getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(JoinTable joinTable) {
        this.joinTable = joinTable;
    }
}
