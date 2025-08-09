package com.lc.mybatisx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 属性不映射到数据库表字段，非持久化字段
 * @author ccxuef
 * @date 2025/8/9 15:28
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface NonPersistent {
}
