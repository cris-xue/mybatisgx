package com.mybatisgx.utils;

import com.mybatisgx.executor.page.Page;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeUtils extends org.apache.commons.lang3.reflect.TypeUtils {

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

    public static Type getCollectionType(ParameterizedType parameterizedType) {
        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
        if (rawType == List.class) {
            return rawType;
        }
        return null;
    }

    public static Type getRawType(ParameterizedType parameterizedType) {
        return parameterizedType.getRawType();
    }

    public static Type getActualType(ParameterizedType parameterizedType) {
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
        return null;
    }

    /**
     * 获取泛型参数和真实类型的映射关系
     * @param type
     * @return
     */
    public static Map<Type, Class<?>> getTypeParameterMap(Type type) {
        Map<Type, Class<?>> typeParameterMap = new HashMap();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            Type rawType = parameterizedType.getRawType();
            TypeVariable[] typeVariableList = getTypeParameters(rawType);

            for (int i = 0; i < typeVariableList.length; i++) {
                TypeVariable typeVariable = typeVariableList[i];
                Type actualTypeArgument = actualTypeArguments[i];
                if (actualTypeArgument instanceof ParameterizedType) {
                    ParameterizedType childActualTypeArgument = (ParameterizedType) actualTypeArgument;
                    Type childRawType = childActualTypeArgument.getRawType();
                    typeParameterMap.put(typeVariable, (Class<?>) childRawType);
                    Map<Type, Class<?>> childTypeParameterMap = getTypeParameterMap(childActualTypeArgument);
                    if (ObjectUtils.isNotEmpty(childTypeParameterMap)) {
                        typeParameterMap.putAll(childTypeParameterMap);
                    }
                }
                if (actualTypeArgument instanceof Class) {
                    typeParameterMap.put(typeVariable, (Class<?>) actualTypeArgument);
                }
            }
        }
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            Type genericSuperclass = clazz.getGenericSuperclass();
            Map<Type, Class<?>> superClassTypeParameterMap = getTypeParameterMap(genericSuperclass);
            if (ObjectUtils.isNotEmpty(superClassTypeParameterMap)) {
                typeParameterMap.putAll(superClassTypeParameterMap);
            }

            Type[] genericInterfaces = clazz.getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                Map<Type, Class<?>> interfaceTypeParameterMap = getTypeParameterMap(genericInterface);
                if (ObjectUtils.isNotEmpty(interfaceTypeParameterMap)) {
                    typeParameterMap.putAll(interfaceTypeParameterMap);
                }
            }
        }
        return typeParameterMap;
    }

    private static TypeVariable[] getTypeParameters(Type rawType) {
        if (rawType instanceof Class) {
            Class<?> clazz = (Class<?>) rawType;
            return clazz.getTypeParameters();
        }
        return null;
    }

    public static Boolean typeEquals(Object object, Class<?> clazz) {
        return object.getClass().equals(clazz);
    }

    public static Boolean typeEquals(Object object, Class<?>... clazzList) {
        for (Class<?> clazz : clazzList) {
            if (typeEquals(object, clazz)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean typeNotEquals(Object object, Class<?> clazz) {
        return !typeEquals(object, clazz);
    }
}
