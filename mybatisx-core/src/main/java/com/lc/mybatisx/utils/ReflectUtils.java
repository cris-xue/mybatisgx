package com.lc.mybatisx.utils;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/12/5 18:14
 */
public class ReflectUtils {

    /**
     * 获取指定字段
     *
     * @param clazz
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     */
    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> superclass = clazz.getSuperclass();
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw e;
        }
        try {
            field = superclass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e1) {
            throw e1;
        }
        return field;
    }

    public static Field[] getDeclaredFields(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

    public static Field[] getSuperDeclaredFields(Class<?> clazz) {
        return clazz.getSuperclass().getDeclaredFields();
    }


    public static Map<String, Object> entityToMap(Class<?> entityClass) {
        Map<String, Object> entityColumn = new LinkedHashMap<>();

        Field[] fields = getDeclaredFields(entityClass);
        Field[] superFields = getSuperDeclaredFields(entityClass);
        arrayToMap(fields, entityColumn);
        arrayToMap(superFields, entityColumn);

        return entityColumn;
    }

    private static void arrayToMap(Field[] fields, Map<String, Object> map) {
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String columnName = field.getName();
            if ("serialVersionUID".equals(columnName)) {
                continue;
            }
            String dbColumnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName);
            map.put(columnName, dbColumnName);
        }
    }

}
