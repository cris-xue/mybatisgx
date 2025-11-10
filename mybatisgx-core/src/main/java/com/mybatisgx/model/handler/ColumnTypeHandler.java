package com.mybatisgx.model.handler;

import com.mybatisgx.model.ClassCategory;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ColumnTypeHandler {

    private static final List<Class<?>> SIMPLE_TYPE_LIST = new ArrayList();
    private static final Map<String, String> TYPE_MAP = new HashMap();

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

    static {
        TYPE_MAP.put("", "");
    }

    public ClassCategory getClassCategory(Type type) {
        for (Class<?> simpleType : SIMPLE_TYPE_LIST) {
            if (type == simpleType) {
                return ClassCategory.SIMPLE;
            }
        }
        return ClassCategory.COMPLEX;
    }
}
