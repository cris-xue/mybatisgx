package com.lc.mybatisx.utils;

import com.lc.mybatisx.dao.Page;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/30 14:00
 */
public class GenericUtils {

    public static Type getGenericType(Type type) {
        if (type instanceof TypeVariable<?>) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            return typeVariable;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypes = parameterizedType.getActualTypeArguments();
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (rawType == List.class) {
                Type actualType = actualTypes[0];
                return getGenericType(actualType);
            } else if (rawType == Page.class) {
                Type actualType = actualTypes[0];
                return getGenericType(actualType);
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
    public static Map<String, Type> getTypeParameterMap(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            return new HashMap();
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        TypeVariable[] typeParameters = clazz.getSuperclass().getTypeParameters();
        Map<String, Type> typeParameterMap = new HashMap();
        for (int i = 0; i < actualTypeArguments.length; i++) {
            Type actualTypeArgument = actualTypeArguments[i];
            TypeVariable typeParameter = typeParameters[i];
            String typeNameKey = String.format("%s%s", clazz.getSimpleName(), typeParameter.getName());
            typeParameterMap.put(typeNameKey, actualTypeArgument);
        }
        return typeParameterMap;
    }
}
