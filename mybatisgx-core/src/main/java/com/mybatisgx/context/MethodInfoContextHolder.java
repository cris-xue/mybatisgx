package com.mybatisgx.context;

import com.mybatisgx.model.MethodInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MethodInfo 静态上下文持有者
 * 用于兼容非 MybatisgxConfiguration 场景（如 MyBatis-Plus）下的 MethodInfo 存取
 *
 * @author ccxuef
 * @date 2026/06/23
 */
public class MethodInfoContextHolder {

    private static final Map<String, MethodInfo> METHOD_INFO_MAP = new ConcurrentHashMap<>();

    public static void set(String msId, MethodInfo methodInfo) {
        METHOD_INFO_MAP.put(msId, methodInfo);
    }

    public static MethodInfo get(String msId) {
        return METHOD_INFO_MAP.get(msId);
    }

    public static List<MethodInfo> getMethodInfoList() {
        return new ArrayList<>(METHOD_INFO_MAP.values());
    }

    public static boolean contains(String msId) {
        return METHOD_INFO_MAP.containsKey(msId);
    }

    public static void remove() {
        METHOD_INFO_MAP.clear();
    }
}
