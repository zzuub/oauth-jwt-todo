package com.domain.todo.exception;

public class ApiException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public ApiException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ApiException(String message) {
        super(message);
        this.exceptionCode = null;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
