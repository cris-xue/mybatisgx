package com.lc.mybatisx.model;

import java.util.HashMap;
import java.util.Map;

public class TableInfoContextHolder {

    private static ThreadLocal<Map<Class<?>, MapperInfo>> mapThreadLocal = new ThreadLocal<>();

    public static void set(Class<?> clazz, MapperInfo mapperInfo) {
        Map<Class<?>, MapperInfo> mapperInfoMap = mapThreadLocal.get();
        if (mapperInfoMap == null) {
            mapperInfoMap = new HashMap<>();
        }
        mapperInfoMap.put(clazz, mapperInfo);
    }

    public static MapperInfo get(Class<?> clazz) {
        Map<Class<?>, MapperInfo> mapperInfoMap = mapThreadLocal.get();
        if (mapperInfoMap == null) {
            return null;
        }
        return mapperInfoMap.get(clazz);
    }

}
