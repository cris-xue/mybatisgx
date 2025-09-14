package com.lc.mybatisx.utils;

import com.lc.mybatisx.dao.Page;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeUtils {

    public static Type getActualTypeArgument(Type type) {
        if (type instanceof TypeVariable<?>) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            return typeVariable;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypes = parameterizedType.getActualTypeArguments();
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (rawType == List.class) {
                Type actualType = actualTypes[0];
                return getActualTypeArgument(actualType);
            } else if (rawType == Page.class) {
                Type actualType = actualTypes[0];
                return getActualTypeArgument(actualType);
            } else if (rawType == Map.class) {
                return rawType;
            }
        } else if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            return clazz;
        }
        return null;
    }

    /**
     * 获取泛型参数和真实类型的映射关系
     * @param clazz
     * @return
     */
    public static Map<String, Class<?>> getTypeParameterMap(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            return new HashMap();
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        TypeVariable[] typeParameters = clazz.getSuperclass().getTypeParameters();
        Map<String, Class<?>> typeParameterMap = new HashMap();
        for (int i = 0; i < actualTypeArguments.length; i++) {
            Type actualTypeArgument = actualTypeArguments[i];
            TypeVariable typeParameter = typeParameters[i];
            typeParameterMap.put(typeParameter.getName(), (Class<?>) actualTypeArgument);
        }
        return typeParameterMap;
    }

    public static Boolean typeEquals(Object object, Class<?> clazz) {
        return object.getClass().equals(clazz);
    }

    public static Boolean typeNotEquals(Object object, Class<?> clazz) {
        return !typeEquals(object, clazz);
    }
}
