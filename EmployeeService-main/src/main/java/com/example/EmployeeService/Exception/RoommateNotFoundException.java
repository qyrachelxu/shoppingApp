package com.example.EmployeeService.Exception;

public class RoommateNotFoundException extends RuntimeException {
    public RoommateNotFoundException(String message) {
        super(message);
    }
}