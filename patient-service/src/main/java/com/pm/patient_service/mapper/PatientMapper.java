package com.pm.patient_service.mapper;

import java.time.LocalDate;
// import java.util.UUID;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.model.Patient;

public class PatientMapper {
    public static PatientResponseDTO toDTO(Patient patient){
        PatientResponseDTO patientDTO = new PatientResponseDTO();
        // using the getter and setters to init the data for the DTO
        patientDTO.setId(patient.getId().toString());
        patientDTO.setAddress(patient.getAddress().toString());
        patientDTO.setEmail(patient.getEmail().toString());
        patientDTO.setName(patient.getName().toString());
        patientDTO.setDateofBirth(patient.getDateOfBirth().toString());

        return patientDTO;
    }
    // return patient object from patient request dto
    public static Patient toModel(PatientRequestDTO patientRequestDTO){
        Patient patient = new Patient(); //calling the constructor
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setName(patientRequestDTO.getName());
        patient.setRegistrationDate(LocalDate.parse(patientRequestDTO.getRegistrationDate()));
        // patient.setId(UUID.randomUUID());
        return patient;
    }
}
