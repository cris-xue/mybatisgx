package com.mybatisgx.executor;

import com.mybatisgx.spi.FieldMeta;

import java.lang.annotation.Annotation;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/4/25 14:20
 */
public class FieldInfo implements FieldMeta {
    @Override
    public String getJavaColumnName() {
        return "";
    }

    @Override
    public Class<?> getJavaType() {
        return null;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return false;
    }
}
