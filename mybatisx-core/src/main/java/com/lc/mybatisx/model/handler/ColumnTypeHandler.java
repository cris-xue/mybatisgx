package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.model.ClassCategory;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ColumnTypeHandler {

    private static final List<Class<?>> SIMPLE_TYPE_LIST = new ArrayList<>();

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

    public ClassCategory getClassCategory(Type type) {
        for (Class<?> simpleType : SIMPLE_TYPE_LIST) {
            if (type == simpleType) {
                return ClassCategory.SIMPLE;
            }
        }
        return ClassCategory.COMPLEX;
    }
}
