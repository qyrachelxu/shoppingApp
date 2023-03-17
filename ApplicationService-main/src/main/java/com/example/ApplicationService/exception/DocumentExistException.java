package com.example.ApplicationService.exception;

public class DocumentExistException  extends RuntimeException {
    public DocumentExistException(String message) {
        super(message);
    }
}
