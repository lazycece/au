package com.lazycece.au.exception;

/**
 * @author lazycece
 * @date 2019/11/8
 */
public class AuException extends RuntimeException {
    public AuException() {
    }

    public AuException(String message) {
        super(message);
    }

    public AuException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuException(Throwable cause) {
        super(cause);
    }

    public AuException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
