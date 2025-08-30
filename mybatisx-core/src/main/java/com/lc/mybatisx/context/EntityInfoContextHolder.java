package com.lc.mybatisx.context;

import com.lc.mybatisx.model.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityInfoContextHolder {

    private static Map<Class<?>, EntityInfo> entityInfoMap = new ConcurrentHashMap<>();

    public static void set(Class<?> clazz, EntityInfo entityInfo) {
        entityInfoMap.put(clazz, entityInfo);
    }

    public static EntityInfo get(Class<?> clazz) {
        return entityInfoMap.get(clazz);
    }

    public static List<Class<?>> getEntityClassList() {
        return new ArrayList(entityInfoMap.keySet());
    }

    public static void remove() {
        entityInfoMap.clear();
    }
}
