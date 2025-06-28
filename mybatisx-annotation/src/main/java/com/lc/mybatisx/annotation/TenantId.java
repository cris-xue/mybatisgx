package com.lc.mybatisx.annotation;

import java.lang.annotation.*;

/**
 * 租户id
 *
 * @author ccxuef
 * @date 2025/6/10 18:18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TenantId {

    /**
     * 是否开启
     *
     * @return
     */
    boolean value() default true;
}
