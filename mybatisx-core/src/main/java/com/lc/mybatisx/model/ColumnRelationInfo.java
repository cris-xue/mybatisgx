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
     * 关系维护方
     */
    private String mappedBy;
    /**
     * 抓取类型
     */
    private String fetchType;
    /**
     * 抓取模式
     */
    private FetchMode fetchMode;
    /**
     * 抓取大小
     */
    private String fetchSize;
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
    /**
     * 抓取方式
     */
    private Fetch fetch;

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    public FetchMode getFetchMode() {
        return fetchMode;
    }

    public void setFetchMode(FetchMode fetchMode) {
        this.fetchMode = fetchMode;
    }

    public String getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(String fetchSize) {
        this.fetchSize = fetchSize;
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
        if (oneToOne != null) {
            this.oneToOne = oneToOne;
            this.mappedBy = oneToOne.mappedBy();
            this.fetchType = oneToOne.fetch().name().toLowerCase();
        }
    }

    public OneToMany getOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(OneToMany oneToMany) {
        if (oneToMany != null) {
            this.oneToMany = oneToMany;
            this.mappedBy = oneToMany.mappedBy();
            this.fetchType = oneToMany.fetch().name().toLowerCase();
        }
    }

    public ManyToOne getManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(ManyToOne manyToOne) {
        if (manyToOne != null) {
            this.manyToOne = manyToOne;
            this.mappedBy = manyToOne.mappedBy();
            this.fetchType = manyToOne.fetch().name().toLowerCase();
        }
    }

    public ManyToMany getManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(ManyToMany manyToMany) {
        if (manyToMany != null) {
            this.manyToMany = manyToMany;
            this.mappedBy = manyToMany.mappedBy();
            this.fetchType = manyToMany.fetch().name().toLowerCase();
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

    public Fetch getFetch() {
        return fetch;
    }

    public void setFetch(Fetch fetch) {
        if (fetch != null) {
            this.fetch = fetch;
            this.fetchMode = fetch.value();
            this.fetchSize = fetch.size() == 0 ? null : Integer.valueOf(fetch.size()).toString();
        }
    }
}
