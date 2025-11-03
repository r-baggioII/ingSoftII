package com.uncuyo.greedy_cars_web.exception;

/**
 * Excepción personalizada para errores en servicios
 */
public class ErrorServiceException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public ErrorServiceException(String message) {
        super(message);
    }

    public ErrorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
