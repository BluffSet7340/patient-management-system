package com.pm.patient_service.exception;

public class PatientNotFoundException extends RuntimeException{
    public PatientNotFoundException(String message){
        super(message); // passes this to the parent, which is the
        // RuntimeException
    }
}
