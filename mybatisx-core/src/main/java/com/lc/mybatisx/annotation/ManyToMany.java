package com.lc.mybatisx.annotation;

import javax.persistence.FetchType;
import java.lang.annotation.*;

import static javax.persistence.FetchType.LAZY;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {

    FetchType fetch() default LAZY;

}
