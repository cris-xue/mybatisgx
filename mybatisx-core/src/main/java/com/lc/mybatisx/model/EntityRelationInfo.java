package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：实体关系信息【user: {roleList: [], orderList: []}】
 * @date ：2021/7/9 17:34
 */
public class EntityRelationInfo {

    /**
     * 关系层级
     */
    private int level;
    /**
     * 字段信息
     */
    private ColumnInfo columnInfo;
    /**
     * 实体信息
     */
    private EntityInfo entityInfo;
    /**
     * 实体关系列表
     */
    private List<EntityRelationInfo> entityRelationList = new ArrayList();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public List<EntityRelationInfo> getEntityRelationList() {
        return entityRelationList;
    }

    public void setEntityRelationList(List<EntityRelationInfo> entityRelationList) {
        this.entityRelationList = entityRelationList;
    }

    public void addEntityRelation(EntityRelationInfo entityRelationInfo) {
        this.entityRelationList.add(entityRelationInfo);
    }
}
