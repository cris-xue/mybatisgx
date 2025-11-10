package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * 查询条件
 *
 * @author ccxuef
 * @date 2025/6/9 10:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface QueryCondition {

    /**
     * aaaAnd(bbbOrCcc)
     *
     * @return
     */
    String value();
}
