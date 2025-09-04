package com.lc.mybatisx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 批量操作数据
 * @author ccxuef
 * @date 2025/9/4 20:52
 */
@Target({PARAMETER})
@Retention(RUNTIME)
public @interface BatchData {

    /**
     * 数据节点参数名
     * @return
     */
    String item() default "batch_data_item";
}
