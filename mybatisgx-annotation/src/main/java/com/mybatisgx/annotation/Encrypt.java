package com.mybatisgx.annotation;

import com.mybatisgx.api.DecryptHandler;
import com.mybatisgx.api.EncryptHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 字段数据加密机
 * @author 薛承城
 * @date 2025/12/10 8:42
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Encrypt {

    Class<EncryptHandler> value();

    Class<? extends DecryptHandler> decrypt() default DecryptHandler.class;
}
