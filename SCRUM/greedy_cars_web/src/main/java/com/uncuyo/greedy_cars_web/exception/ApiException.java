package com.uncuyo.greedy_cars_web.exception;

/**
 * Excepción para errores al comunicarse con la API REST
 */
public class ApiException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    private Integer statusCode;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(String message, Integer statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
