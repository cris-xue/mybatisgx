package com.lc.mybatisx.annotation;

import java.lang.annotation.*;

/**
 * 描述关联字段
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinColumn {

    String name() default "";

    String referencedColumnName() default "id";

    Class<?> table() default Void.class;
}
