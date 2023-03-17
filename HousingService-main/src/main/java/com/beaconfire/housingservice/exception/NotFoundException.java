package com.beaconfire.housingservice.exception;


public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(String.format(message));
    }
}
