package com.lc.mybatisx.annotation;

import java.lang.annotation.*;

/**
 * @author ：薛承城
 * @description：查询实体，作为方法名查询条件的补充，某些情况下，如条件太多会导致方法名太长，使用该方式
 * @date ：2020/11/9 13:17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueryEntity {
}
