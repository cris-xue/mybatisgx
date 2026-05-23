package com.mybatisgx.utils;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/5/5 17:04
 */
public class MethodInfoUtils {

    public static String getNamespaceMethodName(String namespace, String methodName) {
        return String.format("%s.%s", namespace, methodName);
    }
}
