package com.lc.mybatisx.context;

import com.lc.mybatisx.model.TableInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableInfoContextHolder {

    private static Map<Class<?>, TableInfo> tableInfoMap = new ConcurrentHashMap<>();

    public static void set(Class<?> clazz, TableInfo tableInfo) {
        tableInfoMap.put(clazz, tableInfo);
    }

    public static TableInfo get(Class<?> clazz) {
        return tableInfoMap.get(clazz);
    }

}
