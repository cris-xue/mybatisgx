package com.lc.mybatisx.annotation;

import java.lang.annotation.*;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/11/9 13:18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BetweenStart {

    String value();

}
