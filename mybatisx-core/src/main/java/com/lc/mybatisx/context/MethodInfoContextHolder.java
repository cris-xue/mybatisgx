package com.lc.mybatisx.context;

import com.lc.mybatisx.model.MethodInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodInfoContextHolder {

    private static final Map<String, MethodInfo> STATEMENT_METHOD_INFO_MAP = new ConcurrentHashMap<>();

    public static void set(String statement, MethodInfo methodInfo) {
        STATEMENT_METHOD_INFO_MAP.put(statement, methodInfo);
    }

    public static MethodInfo get(String statement) {
        return STATEMENT_METHOD_INFO_MAP.get(statement);
    }

    public static void remove() {
        STATEMENT_METHOD_INFO_MAP.clear();
    }
}
