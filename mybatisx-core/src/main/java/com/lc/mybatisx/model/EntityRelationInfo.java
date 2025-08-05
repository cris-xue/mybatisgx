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
     * 关系层级，第一层为1层，第二层为2层，以此类推，如用户和角色，用户为1、角色为2层
     */
    private int level;
    /**
     * 关系字段，如当前类为role，关系字段为user中的List<Role>字段
     */
    private ColumnInfo columnInfo;
    /**
     * 关联实体，如实体为user、role。如第一层为user，则entityInfo为user,跟entityInfo有关系的实体为entityRelationList中的entityInfo
     */
    private EntityInfo entityInfo;
    /**
     * 实体关系列表，如用户和订单和角色存在关系，entityRelationList为订单和角色
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
