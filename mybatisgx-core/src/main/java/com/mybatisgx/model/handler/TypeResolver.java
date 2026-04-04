package com.mybatisgx.model.handler;

import com.mybatisgx.model.TypeCategory;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TypeResolver {

    private static final List<Class<?>> SIMPLE_TYPE_LIST = new ArrayList();

    static {
        SIMPLE_TYPE_LIST.add(int.class);
        SIMPLE_TYPE_LIST.add(Integer.class);
        SIMPLE_TYPE_LIST.add(Long.class);
        SIMPLE_TYPE_LIST.add(String.class);
        SIMPLE_TYPE_LIST.add(Double.class);
        SIMPLE_TYPE_LIST.add(Boolean.class);
        SIMPLE_TYPE_LIST.add(Date.class);
        SIMPLE_TYPE_LIST.add(LocalDate.class);
        SIMPLE_TYPE_LIST.add(LocalDateTime.class);
    }

    public TypeCategory getClassCategory(Type type) {
        if (!(type instanceof Class<?>)) {
            return TypeCategory.OBJECT;
        }

        Class<?> clazz = (Class<?>) type;

        // 1. 枚举
        if (clazz.isEnum()) {
            return TypeCategory.SIMPLE;
        }

        // 2. 基础类型 / 包装类 / String / Number
        if (isSimpleType(clazz)) {
            return TypeCategory.SIMPLE;
        }

        // 3. 集合类型（你现在没处理，这是个坑）
        if (isCollectionType(clazz)) {
            return TypeCategory.OBJECT;
        }

        return TypeCategory.OBJECT;
    }

    private boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || Number.class.isAssignableFrom(clazz)
                || CharSequence.class.isAssignableFrom(clazz)
                || Boolean.class == clazz
                || Date.class.isAssignableFrom(clazz)
                || clazz.getName().startsWith("java.time");
    }

    private boolean isCollectionType(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || clazz.isArray();
    }
}
