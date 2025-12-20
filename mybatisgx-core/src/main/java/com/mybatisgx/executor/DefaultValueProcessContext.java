package com.mybatisgx.executor;

import com.mybatisgx.api.FieldMeta;
import com.mybatisgx.api.ValueProcessContext;
import com.mybatisgx.api.ValueProcessPhase;
import org.apache.ibatis.reflection.MetaObject;

public class DefaultValueProcessContext implements ValueProcessContext {

    private ValueProcessPhase phase;
    private FieldMeta fieldMeta;
    private Object fieldValue;
    private MetaObject entityMetaObject;

    @Override
    public void init(ValueProcessPhase phase, FieldMeta fieldMeta, Object fieldValue, MetaObject entityMetaObject) {
        this.phase = phase;
        this.fieldMeta = fieldMeta;
        this.fieldValue = fieldValue;
        this.entityMetaObject = entityMetaObject;
    }

    @Override
    public Object getFieldValueAndClear() {
        Object tempFieldValue = this.fieldValue;
        this.phase = null;
        this.fieldMeta = null;
        this.fieldValue = null;
        this.entityMetaObject = null;
        return tempFieldValue;
    }

    @Override
    public ValueProcessPhase getPhase() {
        return phase;
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
