package com.pm.patient_service.mapper;

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
}
