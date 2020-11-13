package com.lc.mybatisx.annotation.dsl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface LeftJoin {

    String value() default "";

    Table aaaa();

}
