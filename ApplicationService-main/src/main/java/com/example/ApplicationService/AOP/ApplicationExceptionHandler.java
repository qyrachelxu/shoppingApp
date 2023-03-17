package com.example.ApplicationService.AOP;


import com.example.ApplicationService.exception.*;
import com.example.ApplicationService.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {


    @ExceptionHandler(value = {ApplicationExistException.class})
    public ResponseEntity<ErrorResponse> handleApplicationExistException(ApplicationExistException e) {
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {DataNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException e) {
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value =  {DocumentExistException.class})
    public ResponseEntity<ErrorResponse> handleDocumentExistException(DocumentExistException e) {
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value =  {InvalidCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value =  {InValidFileInputException.class})
    public ResponseEntity<ErrorResponse> handleInValidFileInputException(InValidFileInputException e) {
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value =  {InvalidInputException.class})
    public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException e) {
        return new ResponseEntity(ErrorResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value =  {JwtAuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleJwtAutenticationException(JwtAuthenticationException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
