package com.xiang.authentication.AOP;

import com.xiang.authentication.domain.response.ErrorResponse;
import com.xiang.authentication.exception.*;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseEntity handleBadCredentialsException(BadCredentialsException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleCustomAuthenticationException(InvalidCredentialsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EmailExistedException.class})
    public ResponseEntity<ErrorResponse> handleEmailExistedException(EmailExistedException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {UsernameExistedException.class})
    public ResponseEntity<ErrorResponse> handleUsernameExistedException(UsernameExistedException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {SignatureException.class})
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {ZeroOrManyException.class})
    public ResponseEntity<ErrorResponse> handleZeroOrManyOrdersException(ZeroOrManyException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {NoPermissionException.class})
    public ResponseEntity<ErrorResponse> handleNoPermissionException(NoPermissionException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {NotEnoughInventoryException.class})
    public ResponseEntity<ErrorResponse> handleNotEnoughInventoryException(NotEnoughInventoryException e){
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }
}
