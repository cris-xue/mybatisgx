package com.mybatisgx.annotation;

import com.mybatisgx.api.GeneratedValueHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
public @interface GeneratedValue {

    Class<? extends GeneratedValueHandler<?>> value();

    boolean insert() default false;

    boolean update() default false;
}
