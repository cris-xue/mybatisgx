package com.mybatisgx.executor;

import com.mybatisgx.spi.FieldMeta;
import com.mybatisgx.spi.ValueProcessCommandType;
import com.mybatisgx.spi.ValueProcessContext;
import org.apache.ibatis.reflection.MetaObject;

public class DefaultValueProcessContext implements ValueProcessContext {

    private ValueProcessCommandType commandType;
    private FieldMeta fieldMeta;
    private Object fieldValue;
    private MetaObject entityMetaObject;

    public DefaultValueProcessContext(ValueProcessCommandType commandType, FieldMeta fieldMeta, Object fieldValue, MetaObject entityMetaObject) {
        this.commandType = commandType;
        this.fieldMeta = fieldMeta;
        this.fieldValue = fieldValue;
        this.entityMetaObject = entityMetaObject;
    }

    @Override
    public ValueProcessCommandType getCommandType() {
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
        if (entityMetaObject.hasGetter(fieldName)) {
            return entityMetaObject.getValue(fieldName);
        }
        return null;
    }
}
