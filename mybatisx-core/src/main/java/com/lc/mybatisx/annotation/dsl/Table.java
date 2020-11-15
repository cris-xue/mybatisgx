package com.lc.mybatisx.annotation.dsl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Table {

    Class<?> name();

    LeftJoin leftJoin() default @LeftJoin;

    Where where() default @Where;

}
