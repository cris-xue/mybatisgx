package com.lc.mybatisx.annotation;

import javax.persistence.FetchType;
import java.lang.annotation.*;

import static javax.persistence.FetchType.LAZY;

/**
 * 描述实体和实体之间的关系
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {

    Class<?> target() default Void.class;

    /**
     * 抓取策略
     *
     * @return
     */
    FetchType fetch() default LAZY;

    /**
     * 关系维护方
     *
     * @return
     */
    String mappedBy() default "";
}
