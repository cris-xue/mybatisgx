package com.lc.mybatisx.utils;

import com.google.common.base.CaseFormat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/12/5 18:14
 */
public class ReflectUtils {

    public static Field getField(Class<?> clazz, Class annotationClass) {
        Field[] fields = getAllField(clazz);
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                return field;
            }
        }
        return null;
    }

    public static Field[] getAllField(Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == Object.class) {
            return clazz.getDeclaredFields();
        } else {
            Field[] classFields = clazz.getDeclaredFields();
            Field[] superClassFields = getAllField(superClass);

            int classFieldLength = classFields.length;
            int superClassFieldLength = superClassFields.length;

            Field[] fields = new Field[classFieldLength + superClassFieldLength];
            System.arraycopy(superClassFields, 0, fields, 0, superClassFieldLength);
            System.arraycopy(classFields, 0, fields, superClassFieldLength, classFieldLength);
            return fields;
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Field[] fields = getAllField(clazz);
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }
        return null;
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
