package com.mybatisgx.model.handler;

import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.ObjectFactory;
import com.mybatisgx.model.PropertyGetter;
import com.mybatisgx.model.PropertySetter;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author：薛承城
 * @description：Lambda访问器工厂
 * @date：2026/5/5 21:15
 */
public class LambdaAccessorFactory {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public static <T> ObjectFactory<T> createObjectFactory(Class<?> clazz) {
        try {
            if (clazz == null) {
                return null;
            }
            if (isNotInitType(clazz)) {
                return null;
            }
            if (clazz.isInterface()) {
                if (clazz == List.class) {
                    return () -> (T) new ArrayList<>();
                }
                if (clazz == Set.class) {
                    return () -> (T) new HashSet<>();
                }
                if (clazz == Map.class) {
                    return () -> (T) new HashMap<>();
                }
                return null;
            }

            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            MethodHandle constructorHandle = LOOKUP.unreflectConstructor(constructor);

            CallSite callSite = LambdaMetafactory.metafactory(
                    LOOKUP,
                    "create",
                    MethodType.methodType(ObjectFactory.class),
                    MethodType.methodType(Object.class),
                    constructorHandle,
                    MethodType.methodType(clazz)
            );

            return (ObjectFactory<T>) callSite.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new MybatisgxException("Failed to create object factory for class: " + clazz, e);
        }
    }

    /**
     * 基础类型、包装类、字符串、枚举、数组、接口、抽象类
     * @param clazz
     * @return
     */
    private static boolean isNotInitType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Boolean.class
                || clazz == Character.class
                || clazz == java.util.Date.class
                || clazz == java.sql.Date.class
                || clazz == java.sql.Timestamp.class
                || clazz == java.time.LocalDate.class
                || clazz == java.time.LocalDateTime.class
                || clazz == java.time.LocalTime.class
                || clazz == java.math.BigDecimal.class
                || clazz == java.math.BigInteger.class
                || clazz.isEnum()
                || clazz.isArray()
                || clazz.isPrimitive()
                || Modifier.isAbstract(clazz.getModifiers())
                || Number.class.isAssignableFrom(clazz);
    }

    public static <T, R> PropertyGetter<T, R> createGetter(Method getterMethod) {
        try {
            getterMethod.setAccessible(true);
            MethodHandle methodHandle = LOOKUP.unreflect(getterMethod);

            CallSite callSite = LambdaMetafactory.metafactory(
                    LOOKUP,
                    "get",
                    MethodType.methodType(PropertyGetter.class),
                    MethodType.methodType(Object.class, Object.class),
                    methodHandle,
                    methodHandle.type()
            );

            return (PropertyGetter<T, R>) callSite.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new MybatisgxException("Failed to create getter lambda for method: " + getterMethod, e);
        }
    }

    public static <T, V> PropertySetter<T, V> createSetter(Method setterMethod) {
        try {
            setterMethod.setAccessible(true);
            MethodHandle methodHandle = LOOKUP.unreflect(setterMethod);

            // 1. 接口方法的通用签名：(Object, Object) -> void
            MethodType samMethodType = MethodType.methodType(void.class, Object.class, Object.class);

            // 2. 运行时方法的具体签名：(目标实体类, 属性具体类) -> void
            // 注意：这里必须手动构造，确保类型精确匹配到方法参数
            MethodType instantiatedMethodType = MethodType.methodType(
                    void.class,
                    setterMethod.getDeclaringClass(),
                    setterMethod.getParameterTypes()[0]
            );

            CallSite callSite = LambdaMetafactory.metafactory(
                    LOOKUP,
                    "set",
                    MethodType.methodType(PropertySetter.class),
                    samMethodType,          // 泛型擦除后的抽象签名
                    methodHandle,
                    instantiatedMethodType  // 运行时具体捕获的签名
            );

            return (PropertySetter<T, V>) callSite.getTarget().invokeExact();
        } catch (Throwable e) {
            throw new MybatisgxException("Failed to create setter lambda for method: " + setterMethod, e);
        }
    }
}
