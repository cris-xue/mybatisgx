package com.mybatisgx.dsl.mgxql.model;

import com.mybatisgx.model.ColumnInfo;

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
     * 字段引用，统一表达三种态：
     * COLUMN 存解析出的字段引用；聚合存聚合参数字段引用；COLUMN_ALL 存 FieldReference(alias, "*")
     */
    private FieldReference fieldRef;
    /**
     * 聚合函数参数类型：FIELD/NUMBER/ASTERISK；COLUMN 与 COLUMN_ALL 为 null
     */
    private AggregateArgumentKind argumentKind;

    public SelectItemType getType() {
        return type;
    }

    public void setType(SelectItemType type) {
        this.type = type;
    }

    public FieldReference getFieldRef() {
        return fieldRef;
    }

    public void setFieldRef(FieldReference fieldRef) {
        this.fieldRef = fieldRef;
    }

    /**
     * 委托 fieldRef.getEntityAlias()，便于调用方沿用旧访问方式
     */
    public String getEntityAlias() {
        return fieldRef != null ? fieldRef.getEntityAlias() : null;
    }

    /**
     * 委托 fieldRef.getFieldName()，便于调用方沿用旧访问方式
     */
    public String getFieldName() {
        return fieldRef != null ? fieldRef.getFieldName() : null;
    }

    /**
     * 委托 fieldRef.getColumnInfo()，便于调用方沿用旧访问方式
     */
    public ColumnInfo getColumnInfo() {
        return fieldRef != null ? fieldRef.getColumnInfo() : null;
    }

    public AggregateArgumentKind getArgumentKind() {
        return argumentKind;
    }

    public void setArgumentKind(AggregateArgumentKind argumentKind) {
        this.argumentKind = argumentKind;
    }
}
