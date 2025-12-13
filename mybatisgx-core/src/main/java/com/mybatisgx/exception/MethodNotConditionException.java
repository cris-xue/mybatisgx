package com.mybatisgx.exception;

/**
 * 方法未添加条件异常
 * @author 薛承城
 * @date 2025/12/13 17:25
 */
public class MethodNotConditionException extends MybatisgxException {

    public MethodNotConditionException(String message, String... args) {
        super(message, args);
    }
}
