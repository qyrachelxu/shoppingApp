package com.example.EmployeeService.Exception;

public class EmployeeNotFoundException extends RuntimeException {
    private Integer userID;

    public EmployeeNotFoundException(Integer userID) {
        this.userID = userID;
    }

    public EmployeeNotFoundException(String msg) {
        super(msg);
    }

    public Integer getUserID() {
        return userID;
    }
}