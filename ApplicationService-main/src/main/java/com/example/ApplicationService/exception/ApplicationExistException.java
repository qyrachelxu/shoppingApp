package com.example.ApplicationService.exception;

public class ApplicationExistException  extends RuntimeException {
    public ApplicationExistException(String message) {
        super(message);
    }

}
