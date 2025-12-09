package com.mybatisgx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 实体属性字段
 * @author 薛承城
 * @date 2025/12/9 12:41
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Column {

    /**
     * 字段名
     * @return
     */
    String name() default "";

    /**
     * 是否允许为空
     * @return
     */
    boolean nullable() default true;

    /**
     * 是否允许插入
     * @return
     */
    boolean insertable() default false;

    /**
     * 是否允许更新
     * @return
     */
    boolean updatable() default false;

    /**
     * 数据库字段类型定义
     * @return
     */
    String columnDefinition() default "";
}
