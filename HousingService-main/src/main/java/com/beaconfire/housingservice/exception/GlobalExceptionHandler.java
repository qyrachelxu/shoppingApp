package com.beaconfire.housingservice.exception;

import com.beaconfire.housingservice.exception.NotLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(NotLoginException.class)
//    public ResponseEntity<String> handleException(NotLoginException e) {
//        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(e.getMessage());
//    }


}
