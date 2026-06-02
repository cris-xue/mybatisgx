package com.mybatisgx.dsl.mgxql.model;

/**
 * MGXQL字段引用模型，用于 ORDER BY / GROUP BY 等子句
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class FieldReference {

    /**
     * 实体别名，如 user（可选）
     */
    private String entityAlias;

    /**
     * 字段名，如 name
     */
    private String fieldName;

    public FieldReference() {
    }

    public FieldReference(String entityAlias, String fieldName) {
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

    /**
     * 获取完整引用路径，如 user.name 或 name
     */
    public String getFullPath() {
        if (entityAlias != null && !entityAlias.isEmpty()) {
            return entityAlias + "." + fieldName;
        }
        return fieldName;
    }
}
