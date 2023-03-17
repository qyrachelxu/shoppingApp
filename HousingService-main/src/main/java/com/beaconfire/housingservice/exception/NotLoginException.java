package com.beaconfire.housingservice.exception;

public class NotLoginException extends RuntimeException{
    public NotLoginException(String message) {
        super(String.format(message));
    }
}
