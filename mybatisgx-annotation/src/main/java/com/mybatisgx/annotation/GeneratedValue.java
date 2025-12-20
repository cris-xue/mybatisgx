package com.mybatisgx.annotation;

import com.mybatisgx.api.ValueProcessor;

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
     * 字段值生成处理器，同一可以设置多个处理器，会按顺序执行，并且一个阶段也支持多个处理
     * @return
     */
    Class<? extends ValueProcessor> value()[];
}
