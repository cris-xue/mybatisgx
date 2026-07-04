package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * 查询字段
 * @author ccxuef
 * @date 2025/11/11 9:54
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface QueryColumn {

    /**
     * apt根据填写的比较操作符，在查询实体中生成对应字段的查询字段,
     * @return
     */
    QueryColumnComparisonOperator[] operator() default {};

    /**
     * 忽略该字段：不作为自动查询条件生成，仅作为手写方法（@Statement / mapper.xml）的参数载体。
     * @return
     */
    boolean ignore() default false;
}
