package com.lc.mybatisx.scripting;

import com.lc.mybatisx.annotation.Column;
import com.lc.mybatisx.utils.ReflectUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public abstract class AbstractMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void fillParameterObject(SqlCommandType sqlCommandType, MetaObject metaObject) {
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
            if (SqlCommandType.INSERT == sqlCommandType && column.insertable()) {
                fieldValue = insert(fieldName, fieldValue, clazz);
            } else if (SqlCommandType.UPDATE == sqlCommandType && column.updatable()) {
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
