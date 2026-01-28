package com.pm.patient_service.dto;
// add the properties that we expect when we receive a request

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PatientRequestDTO {
@NotBlank(message = "Name is required")
@Size(max=100, message ="Name cannot exceed 100 characters")
private String name; 

@NotBlank(message = "Email is required")
@Email(message ="Email should be valid")
private String email;       

@NotBlank(message = "Phone number is required")
private String number; 

@NotBlank(message = "Date of birth is required")
private String dateOfBirth;

@NotBlank(message = "Address is required")
private String Address;

// when the clinician is entering the registration date of a patient, through the frontend perhaps
@NotBlank(message = "Date of registration is required")
private String registrationDate;

}
