package com.mybatisgx.dsl.mgxql.model;

/**
 * MGXQL查询项模型
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class SelectItem {

    /**
     * 查询项类型：COLUMN_ALL, COLUMN, COUNT, MAX, MIN, AVG
     */
    private SelectItemType type;

    /**
     * 实体别名，如 user（可选）
     */
    private String entityAlias;

    /**
     * 字段名或 *
     */
    private String fieldName;

    /**
     * 聚合函数参数字段引用，如 count(user.id) 中的 FieldReference(entityAlias="user", fieldName="id")
     */
    private FieldReference aggregateFieldRef;

    public SelectItemType getType() {
        return type;
    }

    public void setType(SelectItemType type) {
        this.type = type;
    }

    public String getEntityAlias() {
        return entityAlias;
    }

    public void setEntityAlias(String entityAlias) {
        this.entityAlias = entityAlias;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldReference getAggregateFieldRef() {
        return aggregateFieldRef;
    }

    public void setAggregateFieldRef(FieldReference aggregateFieldRef) {
        this.aggregateFieldRef = aggregateFieldRef;
    }
}
