package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：实体关系信息
 * @date ：2021/7/9 17:34
 */
public class EntityRelationInfo {

    /**
     * 关系层级
     */
    private Integer level;
    /**
     * 实体信息
     */
    private EntityInfo entityInfo;
    /**
     * 实体关系列表
     */
    private List<EntityRelationInfo> entityRelationList = new ArrayList();

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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
