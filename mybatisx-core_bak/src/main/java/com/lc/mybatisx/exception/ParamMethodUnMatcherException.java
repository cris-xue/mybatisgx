package com.lc.mybatisx.exception;

public class ParamMethodUnMatcherException extends RuntimeException {

    public ParamMethodUnMatcherException() {
    }

    public ParamMethodUnMatcherException(String message) {
        super(message);
    }

    public ParamMethodUnMatcherException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamMethodUnMatcherException(Throwable cause) {
        super(cause);
    }

    public ParamMethodUnMatcherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
