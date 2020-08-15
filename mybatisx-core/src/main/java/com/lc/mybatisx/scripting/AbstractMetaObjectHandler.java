package com.lc.mybatisx.scripting;

import com.lc.mybatisx.annotation.Column;
import com.lc.mybatisx.utils.ReflectUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public abstract class AbstractMetaObjectHandler implements MetaObjectHandler {

    public void fillParameterObject(MetaObject metaObject) {
        Object params = metaObject.getOriginalObject();
        Field[] fields = ReflectUtils.getAllField(params.getClass());
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }

            String fieldName = field.getName();
            Object fieldValue = metaObject.getValue(fieldName);
            Class<?> clazz = getFieldClass(field);
            if (column.insertable()) {
                fieldValue = insert(fieldName, fieldValue, clazz);
            } else if (column.updatable()) {
                fieldValue = update(fieldName, fieldValue, clazz);
            }

            metaObject.setValue(fieldName, fieldValue);
        }
    }

    private Class<?> getFieldClass(Field field) {
        Type type = field.getType();
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }

}
