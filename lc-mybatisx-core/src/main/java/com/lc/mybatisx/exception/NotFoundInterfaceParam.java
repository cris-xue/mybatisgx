package com.lc.mybatisx.exception;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/21 13:01
 */
public class NotFoundInterfaceParam extends RuntimeException {

    public NotFoundInterfaceParam() {
    }

    public NotFoundInterfaceParam(String message) {
        super(message);
    }

    public NotFoundInterfaceParam(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundInterfaceParam(Throwable cause) {
        super(cause);
    }

    public NotFoundInterfaceParam(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
