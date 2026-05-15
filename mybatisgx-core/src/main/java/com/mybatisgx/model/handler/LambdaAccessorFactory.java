package com.mybatisgx.model.handler;

import com.google.common.collect.Sets;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.ObjectFactory;
import com.mybatisgx.model.PropertyGetter;
import com.mybatisgx.model.PropertySetter;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author：薛承城
 * @description：Lambda访问器工厂
 * @date：2026/5/5 21:15
 */
public class LambdaAccessorFactory {

    private static final Set<Class<?>> SIMPLE_TYPE_SET = Sets.newHashSet(Integer.class, Long.class, String.class);
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public static <T> ObjectFactory<T> createObjectFactory(Class<?> clazz) {
        try {
            Constructor<?> constructor;
            try {
                constructor = clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                return null;
            } catch (SecurityException e) {
                return null;
            }
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
