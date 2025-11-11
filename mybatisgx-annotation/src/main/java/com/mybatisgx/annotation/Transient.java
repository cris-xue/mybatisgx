package com.mybatisgx.annotation;

import java.lang.annotation.*;

/**
 * 标记一个字段不应该被持久化
 * @author 薛承城
 * @date 2025/11/11 10:08
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Transient {
}
