package com.lc.mybatisx.exception;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/21 13:01
 */
public class NotFoundInterfaceParamException extends RuntimeException {

    public NotFoundInterfaceParamException() {
    }

    public NotFoundInterfaceParamException(String message) {
        super(message);
    }

    public NotFoundInterfaceParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundInterfaceParamException(Throwable cause) {
        super(cause);
    }

    public NotFoundInterfaceParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
