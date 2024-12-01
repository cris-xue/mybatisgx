package com.lc.mybatisx.annotation;

import javax.persistence.FetchType;
import java.lang.annotation.*;

import static javax.persistence.FetchType.LAZY;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {

    Class targetEntity() default void.class;

    Class associationEntity() default void.class;

    FetchType fetch() default LAZY;

    /**
     * 外键字段
     *
     * @return
     */
    String[] foreignKey();

    String[] inverseForeignKey();

}
