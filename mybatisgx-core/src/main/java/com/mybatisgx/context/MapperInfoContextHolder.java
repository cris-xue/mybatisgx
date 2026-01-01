package com.mybatisgx.context;

import com.mybatisgx.model.MapperInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapperInfoContextHolder {

    private static final Map<Class<?>, MapperInfo> DAO_CLASS_MAPPER_INFO_MAP = new ConcurrentHashMap<>();

    public static void set(Class<?> clazz, MapperInfo mapperInfo) {
        DAO_CLASS_MAPPER_INFO_MAP.put(clazz, mapperInfo);
    }

    public static MapperInfo get(Class<?> clazz) {
        return DAO_CLASS_MAPPER_INFO_MAP.get(clazz);
    }

    public static List<MapperInfo> getMapperInfoList() {
        return new ArrayList<>(DAO_CLASS_MAPPER_INFO_MAP.values());
    }

    public static void remove() {
        DAO_CLASS_MAPPER_INFO_MAP.clear();
    }
}
