package com.beaconfire.compositeservice.AOP;

import com.beaconfire.compositeservice.exception.InvalidCredentialsException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleFeignException(FeignException e) {
        throw new InvalidCredentialsException("Invalid credentials.");
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNullPointerException(NullPointerException e) {
        throw new InvalidCredentialsException("no credentials.");
    }
}
