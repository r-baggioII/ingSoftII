package com.greedy_cars_institucional.institucional.shared.template.exception;

public class ErrorServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public ErrorServiceException(String message) {
        super(message);
    }

    public ErrorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
