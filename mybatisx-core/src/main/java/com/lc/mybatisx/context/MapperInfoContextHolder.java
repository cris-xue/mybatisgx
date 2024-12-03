package com.lc.mybatisx.context;

import com.lc.mybatisx.model.MapperInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapperInfoContextHolder {

    private static Map<Class<?>, MapperInfo> mapperInfoMap = new ConcurrentHashMap<>();

    public static void set(Class<?> clazz, MapperInfo mapperInfo) {
        mapperInfoMap.put(clazz, mapperInfo);
    }

    public static MapperInfo get(Class<?> clazz) {
        return mapperInfoMap.get(clazz);
    }

}
