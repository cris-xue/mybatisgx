package com.lc.mybatisx.annotation;

import javax.persistence.FetchType;
import java.lang.annotation.*;

import static javax.persistence.FetchType.LAZY;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {

    /**
     * 关系维护实体类型，外键在那个实体，那个实体就是关系维护实体
     *
     * @return
     */
    Class<?> junctionEntity() default Void.class;

    /**
     * 连接的实体
     *
     * @return
     */
    Class<?> joinEntity() default Void.class;

    /**
     * 外键字段
     *
     * @return
     */
    ForeignKey[] foreignKeys() default {};

    FetchType fetch() default LAZY;

    String mappedBy() default "";

}
