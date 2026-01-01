package com.mybatisgx.exception;

/**
 * mybatisgx基础异常
 * @author ccxuef
 * @date 2025/11/5 11:33
 */
public class MybatisgxException extends RuntimeException {

    public MybatisgxException(Throwable cause) {
        super(cause);
    }

    public MybatisgxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MybatisgxException() {
    }

    public MybatisgxException(String message, String... args) {
        super(String.format(message, args));
    }

    public MybatisgxException(String message, Throwable cause, String... args) {
        super(String.format(message, args), cause);
    }
}
