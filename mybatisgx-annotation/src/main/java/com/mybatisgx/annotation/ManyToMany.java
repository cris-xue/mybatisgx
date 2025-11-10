package com.mybatisgx.annotation;

import javax.persistence.FetchType;
import java.lang.annotation.*;

/**
 * 描述实体和实体之间的关系
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {

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
