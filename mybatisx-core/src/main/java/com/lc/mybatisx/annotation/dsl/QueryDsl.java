package com.lc.mybatisx.annotation.dsl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/11/13 16:51
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface QueryDsl {

    Table table();

}
