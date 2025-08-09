package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：实体关系树【user: {roleList: [], orderList: []}】
 * @date ：2021/7/9 17:34
 */
public class EntityRelationTree extends ColumnEntityRelation {

    /**
     * 关系层级，第一层为1层，第二层为2层，以此类推，如用户和角色，用户为1、角色为2层
     */
    private int level;
    /**
     * 实体关系列表，如用户和订单和角色存在关系，entityRelationList为订单和角色
     */
    private List<EntityRelationTree> entityRelationList = new ArrayList();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<EntityRelationTree> getEntityRelationList() {
        return entityRelationList;
    }

    public void setEntityRelationList(List<EntityRelationTree> entityRelationList) {
        this.entityRelationList = entityRelationList;
    }

    public void addEntityRelation(EntityRelationTree entityRelationInfo) {
        this.entityRelationList.add(entityRelationInfo);
    }
}
