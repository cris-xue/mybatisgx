package com.lc.mybatisx.annotation;

public @interface ForeignKey {

    String name() default "";

    String referencedColumnName() default "id";

    Class<?> table() default Void.class;

}
