package com.mybatisgx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 逻辑删除id，用于逻辑删除时，标记数据唯一性
 * @author 薛承城
 * @date 2025/12/8 11:23
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface LogicDeleteId {

    /**
     * 逻辑删除id参数路径
     *
     * @return
     */
    String value() default "logic_delete_id_parameter_key";
}
