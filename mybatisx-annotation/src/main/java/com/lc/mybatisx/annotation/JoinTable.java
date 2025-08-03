package com.lc.mybatisx.annotation;

import java.lang.annotation.*;

/**
 * 描述关联字段，用于多对多的关系
 * @author ccxuef
 * @date 2025/8/3 17:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinTable {

    /**
     * 关联表名
     * @return
     */
    String name();

    /**
     * 当前实体外键
     * @return
     */
    JoinColumn[] joinColumns() default {};

    /**
     * 关联实体外键
     * @return
     */
    JoinColumn[] inverseJoinColumns() default {};
}
