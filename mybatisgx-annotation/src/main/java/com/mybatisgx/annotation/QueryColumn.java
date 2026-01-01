package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * 查询字段
 * @author ccxuef
 * @date 2025/11/11 9:54
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface QueryColumn {

    String value();
}
