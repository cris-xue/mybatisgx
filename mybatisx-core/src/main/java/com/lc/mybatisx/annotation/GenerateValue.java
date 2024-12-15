package com.lc.mybatisx.annotation;

import com.lc.mybatisx.annotation.handler.GenerateValueHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
public @interface GenerateValue {

    Class<? extends GenerateValueHandler<?>> handler();

}
