package com.pm.patient_service.exception;
// custom exception class
public class EmailAlreadyExistsException extends RuntimeException {
// need to add a constructor for this
    public EmailAlreadyExistsException(String message){
        super(message); // passes this to the parent, which is the
        // RuntimeException
    }
}
