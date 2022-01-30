package com.lc.mybatisx.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
            } else if (rawType == Map.class) {
                return rawType;
            }
        } else if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            return clazz;
        }

        return null;
    }

}
