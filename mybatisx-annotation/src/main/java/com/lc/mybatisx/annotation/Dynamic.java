package com.lc.mybatisx.annotation;

import java.lang.annotation.*;

/**
 * @author ：薛承城
 * @description：默认全部字段动态，可通过设置字段来指定需要动态的字段
 * @date ：2020/11/9 13:17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Dynamic {

    /**
     * 需要动态的字段
     *
     * @return
     */
    String[] value() default {};
}
