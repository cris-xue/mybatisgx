package com.lc.mybatisx.utils;

import java.lang.reflect.Field;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/12/5 18:14
 */
public class ReflectUtils {

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
}
