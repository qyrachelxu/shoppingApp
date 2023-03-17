package com.example.EmployeeService.Exception;

public class ValidationNotPassException extends RuntimeException{
    public ValidationNotPassException(String msg) {
        super(msg);
    }
}
