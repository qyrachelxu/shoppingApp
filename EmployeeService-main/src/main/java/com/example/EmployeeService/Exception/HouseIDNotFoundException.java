package com.example.EmployeeService.Exception;

public class HouseIDNotFoundException extends RuntimeException {
    public HouseIDNotFoundException(String message) {
        super(message);
    }
}