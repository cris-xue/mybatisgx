package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * 描述实体之间通过中间表建立的关联关系。用于多对多（Many-to-Many）关系映射
 * @author 薛承城
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
