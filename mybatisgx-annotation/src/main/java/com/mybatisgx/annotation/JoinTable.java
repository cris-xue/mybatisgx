package com.mybatisgx.annotation;

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
     * 中间表表名
     * @return
     */
    String name();

    /**
     * 中间表中，指向当前实体表的外键
     * @return
     */
    JoinColumn[] joinColumns() default {};

    /**
     * 中间表中，指向对端实体表的外键
     * @return
     */
    JoinColumn[] inverseJoinColumns() default {};
}
