package com.pm.patient_service.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.HashMap;
// import org.springframework.http.HttpStatus;


// this is a class that handles exceptions globally
// it will be used to handle exceptions that are thrown by the controller
// it will be used to handle exceptions that are thrown by the service
// it will be used to handle exceptions that are thrown by the repository
// it will be used to handle exceptions that are thrown by the dto
// it will be used to handle exceptions that are thrown by the mapper
// it will be used to handle exceptions that are thrown by the database
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class) // handles validation errors
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>(); // key value pairs of the errors
        // get each of the field errors and then for each error, put the field name and map that to the error in the response body
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class) // annotation
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Email already exists");
        return ResponseEntity.badRequest().body(errors);
    }

}
