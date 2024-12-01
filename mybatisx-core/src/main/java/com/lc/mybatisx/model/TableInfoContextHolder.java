package com.lc.mybatisx.model;

import java.util.HashMap;
import java.util.Map;

public class TableInfoContextHolder {

    private static ThreadLocal<Map<Class<?>, TableInfo>> mapThreadLocal = new ThreadLocal<>();

    public static void set(Class<?> clazz, TableInfo tableInfo) {
        Map<Class<?>, TableInfo> mapperInfoMap = mapThreadLocal.get();
        if (mapperInfoMap == null) {
            mapperInfoMap = new HashMap<>();
            mapThreadLocal.set(mapperInfoMap);
        }
        mapperInfoMap.put(clazz, tableInfo);
    }

    public static TableInfo get(Class<?> clazz) {
        Map<Class<?>, TableInfo> tableInfoMap = mapThreadLocal.get();
        if (tableInfoMap == null) {
            return null;
        }
        return tableInfoMap.get(clazz);
    }

}
