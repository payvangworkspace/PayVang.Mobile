package com.PayVang.Mobile.CustomExceptions;

public class InternalServerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InternalServerException() {
        super();
    }

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
