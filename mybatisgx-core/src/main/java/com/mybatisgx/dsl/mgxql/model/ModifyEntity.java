package com.mybatisgx.dsl.mgxql.model;

/**
 * MGXQL FROM实体模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class ModifyEntity {

    /**
     * 实体名，如 User
     */
    private String entityName;

    public ModifyEntity() {
    }

    public ModifyEntity(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
