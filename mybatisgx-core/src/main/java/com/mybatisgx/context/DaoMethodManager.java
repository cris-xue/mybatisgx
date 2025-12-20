package com.mybatisgx.context;

import com.mybatisgx.api.ValueProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DaoMethodManager {

    private static final Map<Class<ValueProcessor>, ValueProcessor> VALUE_PROCESSOR_MAP = new ConcurrentHashMap<>();

    public static void register(Class<ValueProcessor> clazz) {
        ValueProcessor valueProcessor;
        try {
            valueProcessor = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        VALUE_PROCESSOR_MAP.put(clazz, valueProcessor);
    }

    public static void register(Class<ValueProcessor>[] classes) {
        for (Class<ValueProcessor> clazz : classes) {
            register(clazz);
        }
    }

    public static ValueProcessor get(Class<?> clazz) {
        return VALUE_PROCESSOR_MAP.get(clazz);
    }

    public static List<ValueProcessor> get(Class<?>[] clazzList) {
        List<ValueProcessor> valueProcessors = new ArrayList<>();
        for (Class<?> clazz : clazzList) {
            valueProcessors.add(VALUE_PROCESSOR_MAP.get(clazz));
        }
        return valueProcessors;
    }
}
