package com.lc.mybatisx.annotation;

import java.lang.annotation.*;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/12/5 15:24
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MapperMethod {

    MethodType type();

    boolean dynamic() default false;

}
