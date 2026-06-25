package com.mybatisgx.model;

/**
 * FROM 子句通用节点（entityName + alias），供 FromInfo.primaryEntity 与 JoinInfo 共享
 *
 * @author 薛承城
 * @date 2026/6/12
 */
@Deprecated
public class EntityRefInfo {

    private String entityName;

    private String alias;

    public EntityRefInfo() {
    }

    public EntityRefInfo(String entityName, String alias) {
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
