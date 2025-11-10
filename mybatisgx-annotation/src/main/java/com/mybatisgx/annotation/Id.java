package com.mybatisgx.annotation;

import javax.persistence.GenerationType;
import java.lang.annotation.*;

import static javax.persistence.GenerationType.AUTO;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {

    GenerationType strategy() default AUTO;

}
