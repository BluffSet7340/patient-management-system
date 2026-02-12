package com.pm.patient_service.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;

// this is a class that handles exceptions globally
// it will be used to handle exceptions that are thrown by the controller
// it will be used to handle exceptions that are thrown by the service
// it will be used to handle exceptions that are thrown by the repository
// it will be used to handle exceptions that are thrown by the dto
// it will be used to handle exceptions that are thrown by the mapper
// it will be used to handle exceptions that are thrown by the database
@ControllerAdvice
public class GlobalExceptionHandler {
// log var is init
    private static final Logger log = LoggerFactory.getLogger(
        GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class) // handles validation errors
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>(); // key value pairs of the errors
        // get each of the field errors and then for each error, put the field name and map that to the error in the response body
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class) // annotation
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        // open close braces are placeholders
        // the log is for the developer
        log.warn("Email address already exists {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        // the error is for the client
        errors.put("message", "Email already exists");
        return ResponseEntity.badRequest().body(errors);
        // so one error is for the developer to squash bugs and the other
        // is for the client 
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePatientNotFoundException(PatientNotFoundException ex){
        log.warn("This specific patient could not be found {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Patient not found"); 
        return ResponseEntity.badRequest().body(errors);
    }

}
