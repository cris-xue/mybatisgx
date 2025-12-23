package com.mybatisgx.model;

import com.mybatisgx.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 关系字段信息
 *
 * @author 薛承城
 * @date 2025/7/27 13:20
 */
public class RelationColumnInfo extends ColumnInfo {

    /**
     * 关系维护方，由关系字段类中中的那个字段维护关系
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
     * 关系类型
     */
    private RelationType relationType;
    /**
     * 关系维护方关系字段信息
     */
    private RelationColumnInfo mappedByRelationColumnInfo;
    /**
     * 指向当前实体的外键列表。
     *
     * 语义说明：
     * - 一对一 / 一对多 / 多对一：无（外键不在当前实体视角维护）
     * - 多对多：中间表中指向当前实体主键的一侧外键
     *
     * 说明：
     * 该外键位于中间表中，而非当前实体对应的数据库表。
     */
    private List<ForeignKeyInfo> foreignKeyInfoList = new ArrayList();
    /**
     * 当前实体用于指向关联实体的外键列表。
     *
     * 语义说明：
     * - 一对一 / 一对多 / 多对一：来自 @JoinColumn / @JoinColumns
     * - 多对多：中间表中指向对端实体主键的一侧外键
     */
    private List<ForeignKeyInfo> inverseForeignKeyInfoList = new ArrayList();
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

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
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

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public RelationColumnInfo getMappedByRelationColumnInfo() {
        return mappedByRelationColumnInfo;
    }

    public void setMappedByRelationColumnInfo(RelationColumnInfo mappedByRelationColumnInfo) {
        this.mappedByRelationColumnInfo = mappedByRelationColumnInfo;
    }

    public List<ForeignKeyInfo> getForeignKeyInfoList() {
        return foreignKeyInfoList;
    }

    public void setForeignKeyInfoList(List<ForeignKeyInfo> foreignKeyInfoList) {
        this.foreignKeyInfoList = foreignKeyInfoList;
    }

    public List<ForeignKeyInfo> getInverseForeignKeyInfoList() {
        return inverseForeignKeyInfoList;
    }

    public void setInverseForeignKeyInfoList(List<ForeignKeyInfo> inverseForeignKeyInfoList) {
        this.inverseForeignKeyInfoList = inverseForeignKeyInfoList;
    }

    public OneToOne getOneToOne() {
        return oneToOne;
    }

    public void setOneToOne(OneToOne oneToOne) {
        if (oneToOne != null) {
            this.oneToOne = oneToOne;
            this.mappedBy = oneToOne.mappedBy();
            this.fetchType = oneToOne.fetch().name().toLowerCase();
            this.relationType = RelationType.ONE_TO_ONE;
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
            this.relationType = RelationType.ONE_TO_MANY;
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
            this.relationType = RelationType.MANY_TO_ONE;
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
            this.relationType = RelationType.MANY_TO_MANY;
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
