package com.lc.mybatisx.annotation;

import java.lang.annotation.*;

/**
 * 描述关联字段，用于多对多的关系
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinTable {

    String name();

    JoinColumn[] joinColumns() default {};

    JoinColumn[] inverseJoinColumns() default {};

}
