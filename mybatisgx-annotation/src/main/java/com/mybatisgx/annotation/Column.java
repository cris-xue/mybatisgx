package com.mybatisgx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于描述实体属性与数据库表字段之间的映射关系。
 *
 * 该注解声明当前实体属性对应的数据库列信息，
 * 主要用于 ORM 在生成 SQL 及结果映射时使用。
 *
 * 若不写该注解，或未显式指定字段名，
 * 数据库字段默认使用属性名下划线命名规则进行转换。
 * @author 薛承城
 * @date 2025/12/9 12:41
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Column {

    /**
     * 对应的数据库字段名称
     * @return
     */
    String name() default "";

    /**
     * 是否允许为空
     * @return
     */
    // boolean nullable() default true;

    /**
     * 是否允许插入
     * @return
     */
    // boolean insertable() default false;

    /**
     * 是否允许更新
     * @return
     */
    // boolean updatable() default false;

    /**
     * 数据库字段的类型定义描述，仅作为元信息使用。
     *
     * 不用于自动建表，仅用于 SQL 生成或特殊场景下的字段处理。
     * @return
     */
    String columnDefinition() default "";
}
