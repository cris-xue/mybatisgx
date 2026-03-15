package com.mybatisgx.context;

import com.mybatisgx.model.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityInfoContextHolder {

    private static Map<Class<?>, EntityInfo> ENTITY_INFO_MAP = new ConcurrentHashMap<>();

    public static void set(Class<?> clazz, EntityInfo entityInfo) {
        ENTITY_INFO_MAP.put(clazz, entityInfo);
    }

    public static EntityInfo get(Class<?> clazz) {
        return ENTITY_INFO_MAP.get(clazz);
    }

    public static List<Class<?>> getEntityClassList() {
        return new ArrayList(ENTITY_INFO_MAP.keySet());
    }

    public static void remove() {
        ENTITY_INFO_MAP.clear();
    }
}
