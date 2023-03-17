package com.example.EmployeeService.AOP;

import com.example.EmployeeService.Exception.*;
import com.example.EmployeeService.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = {EmployeeNotFoundException.class})
    public ResponseEntity<MessageResponse> handleEmployeeNotFoundException(EmployeeNotFoundException e){
        return new ResponseEntity(MessageResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {ValidationNotPassException.class})
    public ResponseEntity<MessageResponse> handleValidationNotPassException(ValidationNotPassException e){
        return new ResponseEntity(MessageResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {DocumentNotFoundException.class})
    public ResponseEntity<MessageResponse> handleDocumentNotFoundException(DocumentNotFoundException e){
        return new ResponseEntity(MessageResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {RoommateNotFoundException.class})
    public ResponseEntity<MessageResponse> handleRoommateNotFoundException(RoommateNotFoundException e){
        return new ResponseEntity(MessageResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {EmailNotFoundException.class})
    public ResponseEntity<MessageResponse> handleEmailNotFoundException(EmailNotFoundException e){
        return new ResponseEntity(MessageResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

    @ExceptionHandler(value = {HouseIDNotFoundException.class})
    public ResponseEntity<MessageResponse> handleHouseIDNotFoundException(HouseIDNotFoundException e){
        return new ResponseEntity(MessageResponse.builder().message(e.getMessage()).build(), HttpStatus.OK);
    }

}
