package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.*;

import java.lang.reflect.Type;

/**
 * 列信息
 */
public class ColumnInfo {

    /**
     *
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
    private Class<?> containerType;
    /**
     * 容器类型名
     */
    private String containerTypeName;
    /**
     *
     */
    private String dbTypeName;
    /**
     *
     */
    private String dbColumnName;
    /**
     * 类型处理器
     */
    private String typeHandler;
    /**
     * 是否是主键
     */
    private Id id;
    /**
     * 是否是乐观锁字段
     */
    private Lock lock;
    /**
     * 是否是逻辑删除字段
     */
    private LogicDelete logicDelete;
    /**
     * 是否是外键
     */
    private Boolean foreignKey = false;
    /**
     * 关系维护方
     */
    private String mappedBy;
    /**
     * 是否关联查询
     */
    private Boolean associationSelect = false;
    /**
     * 关联字段
     */
    private JoinColumn joinColumn;
    /**
     * 关联表
     */
    private JoinTable joinTable;
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

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Type javaType) {
        if (javaType instanceof Class) {
            this.javaType = (Class<?>) javaType;
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

    public Class<?> getContainerType() {
        return containerType;
    }

    public void setContainerType(Class<?> containerType) {
        this.containerType = containerType;
        this.containerTypeName = containerType.getName();
    }

    public String getContainerTypeName() {
        return containerTypeName;
    }

    public void setContainerTypeName(String containerTypeName) {
        this.containerTypeName = containerTypeName;
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

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
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

    public Boolean getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(Boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    public Boolean getAssociationSelect() {
        return associationSelect;
    }

    public void setAssociationSelect(Boolean associationSelect) {
        this.associationSelect = associationSelect;
    }

    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public void setJoinColumn(JoinColumn joinColumn) {
        this.joinColumn = joinColumn;
        if (joinColumn != null) {
            this.dbColumnName = joinColumn.name();
            this.foreignKey = true;
        }
    }

    public JoinTable getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(JoinTable joinTable) {
        this.joinTable = joinTable;
    }

    public ManyToMany getManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(ManyToMany manyToMany) {
        this.manyToMany = manyToMany;
        if (manyToMany != null) {
            this.mappedBy = manyToMany.mappedBy();
            this.associationSelect = true;
        }
    }

    public ManyToOne getManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(ManyToOne manyToOne) {
        this.manyToOne = manyToOne;
        if (manyToOne != null) {
            this.associationSelect = true;
        }
    }

    public OneToOne getOneToOne() {
        return oneToOne;
    }

    public void setOneToOne(OneToOne oneToOne) {
        this.oneToOne = oneToOne;
        if (oneToOne != null) {
            this.mappedBy = oneToOne.mappedBy();
            this.associationSelect = true;
        }
    }

    public OneToMany getOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(OneToMany oneToMany) {
        this.oneToMany = oneToMany;
        if (oneToMany != null) {
            this.mappedBy = oneToMany.mappedBy();
            this.associationSelect = true;
        }
    }
}
