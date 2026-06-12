package com.mybatisgx.model;

/**
 * GROUP BY 字段引用目标模型，不复用 DSL 层的 FieldReference
 *
 * @author 薛承城
 * @date 2026/6/12
 */
public class GroupByFieldInfo {

    private String entityAlias;

    private String fieldName;

    public GroupByFieldInfo() {
    }

    public GroupByFieldInfo(String entityAlias, String fieldName) {
        this.entityAlias = entityAlias;
        this.fieldName = fieldName;
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
}
