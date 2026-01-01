package com.mybatisgx.annotation;

import org.apache.ibatis.mapping.FetchType;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {

    /**
     * 抓取策略
     *
     * @return
     */
    FetchType fetch() default FetchType.LAZY;

    /**
     * 关系维护方
     *
     * @return
     */
    String mappedBy() default "";
}
