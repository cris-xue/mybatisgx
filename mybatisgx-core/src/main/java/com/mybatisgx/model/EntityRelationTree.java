package com.mybatisgx.model;

/**
 * @author ：薛承城
 * @description：实体关系树【user: {roleList: [], orderList: []}】
 * @date ：2021/7/9 17:34
 */
public class EntityRelationTree extends ColumnEntityRelation<EntityRelationTree> {

    /**
     * 关系层级，第一层为1层，第二层为2层，以此类推，如用户和角色，用户为1、角色为2层
     */
    private int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
