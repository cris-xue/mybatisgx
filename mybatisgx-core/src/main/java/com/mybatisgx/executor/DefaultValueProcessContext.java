package com.mybatisgx.executor;

import com.mybatisgx.api.MethodCommandType;
import com.mybatisgx.spi.FieldMeta;
import com.mybatisgx.spi.ValueProcessContext;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

public class DefaultValueProcessContext implements ValueProcessContext {

    private MethodCommandType commandType;
    private FieldMeta fieldMeta;
    private Object fieldValue;
    private Object parameterObject;
    private MetaObject entityMetaObject;

    public DefaultValueProcessContext(MethodCommandType commandType, FieldMeta fieldMeta, Object fieldValue, Object parameterObject) {
        this.commandType = commandType;
        this.fieldMeta = fieldMeta;
        this.fieldValue = fieldValue;
        this.parameterObject = parameterObject;
    }

    @Override
    public MethodCommandType getCommandType() {
        return commandType;
    }

    @Override
    public FieldMeta getFieldMeta() {
        return fieldMeta;
    }

    @Override
    public Object getFieldValue() {
        return this.fieldValue;
    }

    @Override
    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public Object getFieldValue(String fieldName) {
        if (entityMetaObject == null && parameterObject != null) {
            this.entityMetaObject = SystemMetaObject.forObject(parameterObject);
        }
        if (entityMetaObject.hasGetter(fieldName)) {
            return entityMetaObject.getValue(fieldName);
        }
        return null;
    }
}
