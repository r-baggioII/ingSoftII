package com.example.demoApiRestConsumer.business.logic.error;

public class ErrorServiceException extends Exception {
    
    public ErrorServiceException() {}

    public ErrorServiceException(String msg) {
        super(msg);
    }
}

