package com.mybatisgx.dsl.mgxql.model;

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
}
