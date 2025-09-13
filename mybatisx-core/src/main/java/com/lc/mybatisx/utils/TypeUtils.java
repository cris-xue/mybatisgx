package com.lc.mybatisx.utils;

public class TypeUtils {

    public static Boolean typeEquals(Object object, Class<?> clazz) {
        return object.getClass().equals(clazz);
    }
}
