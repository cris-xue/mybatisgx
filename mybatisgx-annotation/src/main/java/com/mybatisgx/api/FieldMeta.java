package com.mybatisgx.api;

import java.lang.annotation.Annotation;

/**
 * 字段元信息接口
 * @author 薛承城
 * @date 2025/12/20 18:03
 */
public interface FieldMeta {

    String getJavaColumnName();

    Class<?> getJavaType();

    <A extends Annotation> A getAnnotation(Class<A> annotationType);

    boolean hasAnnotation(Class<? extends Annotation> annotationType);
}
