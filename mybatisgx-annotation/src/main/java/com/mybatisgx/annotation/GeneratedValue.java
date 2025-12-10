package com.mybatisgx.annotation;

import com.mybatisgx.api.GeneratedValueHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 值生成注解
 * @author 薛承城
 * @date 2025/12/9 14:05
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface GeneratedValue {

    /**
     * 字段值生成处理器
     * @return
     */
    Class<? extends GeneratedValueHandler<?>> value();

    /**
     * 是否插入时生成
     * @return
     */
    boolean insert() default false;

    /**
     * 是否更新时生成
     * @return
     */
    boolean update() default false;
}
