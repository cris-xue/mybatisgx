package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * 一句话描述
 *
 * @author ccxuef
 * @date 2025/6/9 10:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ConditionGroup {

    /**
     * aaaAnd(bbbOrCcc)
     *
     * @return
     */
    String value();

    // ConditionGroup[] value();
}
