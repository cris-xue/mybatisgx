package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：关联表信息，多对多中间关联表没有太大的以意义，完全是为了关系型数据库考虑。但有时候需要处理关联问题的开关，又有一定的作用，所以需要当作正常表来处理
 * @date ：2021/7/9 17:14
 */
public class AssociationEntityInfo {

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
    private Integer fetchSize;
    /**
     * 关联字段
     */
    private String name;
    /**
     * 关联字段
     */
    private String referencedColumnName;
    /**
     * 外键字段列表
     */
    private List<ColumnInfo> foreignKeyColumnInfoList = new ArrayList();
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

    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    public List<ColumnInfo> getForeignKeyColumnInfoList() {
        return foreignKeyColumnInfoList;
    }

    public void setForeignKeyColumnInfoList(List<ColumnInfo> foreignKeyColumnInfoList) {
        this.foreignKeyColumnInfoList = foreignKeyColumnInfoList;
    }

    public OneToOne getOneToOne() {
        return oneToOne;
    }

    public void setOneToOne(OneToOne oneToOne) {
        this.oneToOne = oneToOne;
        if (oneToOne != null) {
            this.mappedBy = oneToOne.mappedBy();
            this.fetch = oneToOne.fetch().name();
            this.fetchSize = oneToOne.fetchSize();
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
            this.fetchSize = oneToMany.fetchSize();
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
            this.fetchSize = manyToOne.fetchSize();
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
            this.fetchSize = manyToMany.fetchSize();
        }
    }

    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public void setJoinColumn(JoinColumn joinColumn) {
        this.joinColumn = joinColumn;
        if (this.joinColumn != null) {
            this.name = this.joinColumn.name();
            this.referencedColumnName = this.joinColumn.referencedColumnName();
        }
    }

    public JoinTable getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(JoinTable joinTable) {
        this.joinTable = joinTable;
    }
}
