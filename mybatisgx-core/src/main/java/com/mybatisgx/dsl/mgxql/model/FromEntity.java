package com.mybatisgx.dsl.mgxql.model;

import com.mybatisgx.model.EntityInfo;

/**
 * MGXQL FROM实体模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class FromEntity {

    /**
     * 实体名，如 User
     */
    private String entityName;
    /**
     * 别名，如 user
     */
    private String alias;
    /**
     * 语义校验阶段绑定的实体元数据，FROM 渲染直接读取表名
     */
    private EntityInfo entityInfo;

    public FromEntity() {
    }

    public FromEntity(String entityName, String alias) {
        this.entityName = entityName;
        this.alias = alias;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }
}
