package com.mybatisgx.utils;

import com.mybatisgx.exception.MybatisgxException;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/5/5 21:36
 */
public class BeanMethodUtils {

    public static Map<String, PropertyDescriptor> getGetterMethodMap(Class<?> clazz) {
        return getStringPropertyDescriptorMap(clazz);
    }

    public static Map<String, PropertyDescriptor> getSetterMethodMap(Class<?> clazz) {
        return getStringPropertyDescriptorMap(clazz);
    }

    @NonNull
    private static Map<String, PropertyDescriptor> getStringPropertyDescriptorMap(Class<?> clazz) {
        try {
            Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            }
            return propertyDescriptorMap;
        } catch (Exception e) {
            throw new MybatisgxException(e);
        }
    }
}
