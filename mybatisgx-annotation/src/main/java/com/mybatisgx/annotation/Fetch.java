package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * 抓取策略
 * @author ccxuef
 * @date 2025/9/6 15:14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Fetch {

    /**
     * 抓取模式
     * @return
     */
    FetchMode value() default FetchMode.BATCH;

    /**
     * 抓取大小（0为全部）
     * @return
     */
    int size() default 0;
}
