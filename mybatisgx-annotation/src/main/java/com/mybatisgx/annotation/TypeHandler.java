package com.mybatisgx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * mybatis类型处理器
 * @author 薛承城
 * @date 2025/12/9 12:10
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface TypeHandler {

    Class<?> value();
}
