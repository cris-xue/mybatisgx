package com.lc.mybatisx.annotation;

import javax.persistence.FetchType;

import static javax.persistence.FetchType.LAZY;

public @interface JoinTable {

    Class targetEntity() default void.class;

    FetchType fetch() default LAZY;

    JoinColumn[] joinColumns() default {};

}
