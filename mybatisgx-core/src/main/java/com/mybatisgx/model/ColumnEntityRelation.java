package com.mybatisgx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 字段实体关系
 *
 * @author ccxuef
 * @date 2025/8/9 18:03
 */
public class ColumnEntityRelation<T> {

    /**
     * 关系字段，如当前类为role，关系字段为user中的List<Role>字段
     */
    protected ColumnInfo columnInfo;
    /**
     * 中间实体，只有多对多场景才会存在
     */
    private MiddleEntityInfo middleEntityInfo;
    /**
     * 关联实体，如实体为user、role。如第一层为user，则entityInfo为user,跟entityInfo有关系的实体为entityRelationList中的entityInfo
     */
    protected EntityInfo entityInfo;
    /**
     * 结果集和关系查询中的子对象
     */
    private List<T> composites = new ArrayList();

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    public MiddleEntityInfo getMiddleEntityInfo() {
        return middleEntityInfo;
    }

    public void setMiddleEntityInfo(MiddleEntityInfo middleEntityInfo) {
        this.middleEntityInfo = middleEntityInfo;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public ColumnInfo getEntityColumnInfo(String javaColumnName) {
        return this.entityInfo.getColumnInfo(javaColumnName);
    }

    public List<T> getComposites() {
        return composites;
    }

    public void setComposites(List<T> composites) {
        this.composites = composites;
    }

    public void addComposites(T composite) {
        this.composites.add(composite);
    }

    public Class<?> getEntityClazz() {
        return this.entityInfo.getClazz();
    }

    public String getEntityClazzName() {
        return this.entityInfo.getClazzName();
    }

    public List<ColumnInfo> getTableColumnInfoList() {
        return this.entityInfo.getTableColumnInfoList();
    }

    /**
     * 当前实体是否是关系维护方
     *
     * @return true：是关系维护方 false：关系被维护方
     */
    public Boolean isMappedBy() {
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) this.columnInfo;
        RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
        // 关联字段中mappedBy != null说明关系在实体中维护，那么当前实体就是关系维护方
        return mappedByRelationColumnInfo != null;
    }

    /**
     * 是否是多对多关系
     *
     * @return true: 表示是多对多关系 false: 表示不是多对多关系
     */
    public Boolean isManyToMany() {
        if (this.columnInfo instanceof RelationColumnInfo) {
            return ((RelationColumnInfo) this.columnInfo).getRelationType() == RelationType.MANY_TO_MANY;
        }
        return false;
    }

    public List<ForeignKeyColumnInfo> getForeignKeyColumnInfoList() {
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) this.columnInfo;
        RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
        if (mappedByRelationColumnInfo != null) {
            return mappedByRelationColumnInfo.getForeignKeyColumnInfoList();
        } else {
            return relationColumnInfo.getForeignKeyColumnInfoList();
        }
    }

    public List<ForeignKeyColumnInfo> getInverseForeignKeyColumnInfoList() {
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) this.columnInfo;
        RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
        if (mappedByRelationColumnInfo != null) {
            return mappedByRelationColumnInfo.getInverseForeignKeyColumnInfoList();
        } else {
            return relationColumnInfo.getInverseForeignKeyColumnInfoList();
        }
    }
}
