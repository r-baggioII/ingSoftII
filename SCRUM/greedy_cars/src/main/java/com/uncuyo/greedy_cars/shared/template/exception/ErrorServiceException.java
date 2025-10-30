package com.uncuyo.greedy_cars.shared.template.exception;

/**
 * Runtime exception used across template services to communicate business
 * failures while keeping the original stack trace when available.
 */
public class ErrorServiceException extends RuntimeException {

    public ErrorServiceException(String message) {
        super(message);
    }

    public ErrorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
