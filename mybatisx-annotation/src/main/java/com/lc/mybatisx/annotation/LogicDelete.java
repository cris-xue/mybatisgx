package com.lc.mybatisx.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/11/9 16:42
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface LogicDelete {

    String show() default "1";

    String hide() default "0";

}
