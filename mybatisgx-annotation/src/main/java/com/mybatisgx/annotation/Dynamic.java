package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * @author ：薛承城
 * @description：sql动态，触发条件是字段是否为空，动态范围如下：
 * 新增：所有插入字段动态
 * 修改：所有修改字段动态和所有条件动态
 * 删除：所有条件动态
 * 查询：所有条件动态
 * @date ：2020/11/9 13:17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Dynamic {
}
