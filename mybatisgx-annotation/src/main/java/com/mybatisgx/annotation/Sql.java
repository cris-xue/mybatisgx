package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * sql定义，支持方法名衍生和HQL
 * @author 薛承城
 * @date 2025/11/11 11:22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Sql {

    /**
     * findByAaaAnd(bbbOrCcc)、updateByIdAndName(bbbOrCcc)
     * @return
     */
    String value();

    /**
     * sql语法类型
     * @return
     */
    SqlSyntaxType type() default SqlSyntaxType.METHOD_NAME;
}
