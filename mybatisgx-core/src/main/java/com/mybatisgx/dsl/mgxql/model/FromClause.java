package com.mybatisgx.dsl.mgxql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * MGXQL FROM子句模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class FromClause {

    /**
     * 主实体
     */
    private FromEntity primaryEntity;

    /**
     * JOIN实体列表
     */
    private List<JoinEntity> joinEntities = new ArrayList<>();

    public FromEntity getPrimaryEntity() {
        return primaryEntity;
    }

    public void setPrimaryEntity(FromEntity primaryEntity) {
        this.primaryEntity = primaryEntity;
    }

    public List<JoinEntity> getJoinEntities() {
        return joinEntities;
    }

    public void setJoinEntities(List<JoinEntity> joinEntities) {
        this.joinEntities = joinEntities;
    }

    public void addJoinEntity(JoinEntity joinEntity) {
        this.joinEntities.add(joinEntity);
    }
}
