package com.example.ApplicationService.exception;

public class ApplicationIncompleteException  extends RuntimeException {
    public ApplicationIncompleteException(String message) {
        super(message);
    }
}
