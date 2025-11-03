package com.gredy_cars_client.gredy_cars_client.shared.template.exception;

/**
 * Checked exception representing business or communication issues raised while
 * orchestrating template operations in the client.
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

