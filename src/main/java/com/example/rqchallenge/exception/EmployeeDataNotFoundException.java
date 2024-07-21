package com.example.rqchallenge.exception;

public class EmployeeDataNotFoundException extends RuntimeException{

    public EmployeeDataNotFoundException(String message) {
        super(message);
    }

    public EmployeeDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
