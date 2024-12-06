package com.lc.mybatisx.annotation;

import javax.persistence.FetchType;
import java.lang.annotation.*;

import static javax.persistence.FetchType.LAZY;

/**
 * 描述实体和实体之间的关系
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {

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

    /**
     * 另一张表外键字段
     *
     * @return
     */
    ForeignKey[] inverseForeignKeys() default {};

    /**
     * 抓取策略
     *
     * @return
     */
    FetchType fetch() default LAZY;

    String mappedBy() default "";

}
