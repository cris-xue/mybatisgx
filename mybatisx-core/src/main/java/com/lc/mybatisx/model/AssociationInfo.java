package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.*;

/**
 * @author ：薛承城
 * @description：关联表信息，多对多中间关联表没有太大的以意义，完全是为了关系型数据库考虑。但有时候需要处理关联问题的开关，又有一定的作用，所以需要当作正常表来处理
 * @date ：2021/7/9 17:14
 */
public class AssociationInfo {

    private String mappedBy;
    private String fetch;
    private String name;
    private String referencedColumnName;
    /**
     * 多对多
     */
    private ManyToMany manyToMany;
    /**
     * 多对一
     */
    private ManyToOne manyToOne;
    /**
     * 一对一
     */
    private OneToOne oneToOne;
    /**
     * 一对多
     */
    private OneToMany oneToMany;
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

    public ManyToMany getManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(ManyToMany manyToMany) {
        this.manyToMany = manyToMany;
    }

    public ManyToOne getManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(ManyToOne manyToOne) {
        this.manyToOne = manyToOne;
    }

    public OneToOne getOneToOne() {
        return oneToOne;
    }

    public void setOneToOne(OneToOne oneToOne) {
        this.oneToOne = oneToOne;
        if (this.oneToOne != null) {
            this.mappedBy = oneToOne.mappedBy();
            this.fetch = oneToOne.fetch().name();
        }
    }

    public OneToMany getOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(OneToMany oneToMany) {
        this.oneToMany = oneToMany;
    }

    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public void setJoinColumn(JoinColumn joinColumn) {
        this.joinColumn = joinColumn;
        if (this.joinColumn != null) {
            this.name = joinColumn.name();
        }
    }

    public JoinTable getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(JoinTable joinTable) {
        this.joinTable = joinTable;
    }
}
