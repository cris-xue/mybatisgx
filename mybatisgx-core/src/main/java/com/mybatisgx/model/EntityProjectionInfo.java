package com.mybatisgx.model;

/**
 * 实体投影信息，描述部分实体字段的模型
 * @author 薛承城
 * @date 2026/7/2 20:20
 */
public class EntityProjectionInfo extends EntityInfo {

    /**
     * 为投影时的实体类型
     */
    private Class<?> entityClass;

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }
}
