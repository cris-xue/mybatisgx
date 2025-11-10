package com.mybatisgx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 联合主键
 * @author ccxuef
 * @date 2025/9/12 20:14
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface EmbeddedId {
}
