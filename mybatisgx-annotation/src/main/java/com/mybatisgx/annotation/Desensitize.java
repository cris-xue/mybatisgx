package com.mybatisgx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 数据脱敏
 * @author 薛承城
 * @date 2025/12/10 8:41
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Desensitize {
}
