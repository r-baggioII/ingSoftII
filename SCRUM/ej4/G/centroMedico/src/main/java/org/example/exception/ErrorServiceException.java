package org.example.exception;

public class ErrorServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public ErrorServiceException() {
        super();
    }

    public ErrorServiceException(String message) {
        super(message);
    }

    public ErrorServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorServiceException(Throwable cause) {
        super(cause);
    }
}
