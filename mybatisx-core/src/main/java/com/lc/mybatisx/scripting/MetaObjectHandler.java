package com.lc.mybatisx.scripting;

import com.lc.mybatisx.utils.ReflectUtils;
import org.apache.ibatis.reflection.MetaObject;

import javax.persistence.Column;
import java.lang.reflect.Field;

public interface MetaObjectHandler extends Cloneable {

    default void insert(MetaObject metaObject) {
        Object params = metaObject.getOriginalObject();
        Field[] fields = ReflectUtils.getAllField(params.getClass());
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.insertable()) {
                String fieldName = field.getName();
                Object fieldValue = metaObject.getValue(fieldName);
                fieldValue = insert(fieldName, fieldValue);
                metaObject.setValue(fieldName, fieldValue);
            }
        }
    }

    default void update(MetaObject metaObject) {
        Object params = metaObject.getOriginalObject();
        Field[] fields = ReflectUtils.getAllField(params.getClass());
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.updatable()) {
                String fieldName = field.getName();
                Object fieldValue = metaObject.getValue(fieldName);
                fieldValue = update(fieldName, fieldValue);
                metaObject.setValue(fieldName, fieldValue);
            }
        }
    }

    Object insert(String field, Object object);

    Object update(String field, Object object);

}
