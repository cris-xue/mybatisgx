package com.lc.mybatisx.scripting;

import com.lc.mybatisx.utils.ReflectUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface MetaObjectHandler extends Cloneable {

    default void fillParameterObject(SqlCommandType sqlCommandType, MetaObject metaObject) {
        Object params = metaObject.getOriginalObject();
        Field[] fields = ReflectUtils.getAllField(params.getClass());
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.insertable()) {
                String fieldName = field.getName();
                Type type = field.getType();
                Class<?> clazz = null;
                if (type instanceof Class<?>) {
                    clazz = (Class<?>) type;
                }

                Object fieldValue = metaObject.getValue(fieldName);
                if (SqlCommandType.INSERT == sqlCommandType) {
                    fieldValue = insert(fieldName, fieldValue, clazz);
                } else if (SqlCommandType.UPDATE == sqlCommandType) {
                    fieldValue = update(fieldName, fieldValue, clazz);
                }

                metaObject.setValue(fieldName, fieldValue);
            }
        }
    }

    Object insert(String field, Object object, Class<?> clazz);

    Object update(String field, Object object, Class<?> clazz);

}
