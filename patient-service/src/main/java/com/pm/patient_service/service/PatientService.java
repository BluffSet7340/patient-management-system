package com.pm.patient_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.mapper.PatientMapper;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    // this is a constructor I believe 
    public PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    // service layer converts domain entity model into DTO
    public List<PatientResponseDTO> getPatients(){
        List<Patient> allPatients =  patientRepository.findAll(); // gives me the list of all patient inside of the patientRepository
        // lambda function -> gets the list of patients and uses the map function to take each patient, convert each patient to a DTO object put all dtos in a list
        List<PatientResponseDTO> patientResponseDTOs = 
            allPatients.stream().map(patient -> PatientMapper.toDTO(patient)).toList();
// returns a list of patientdtos
        return patientResponseDTOs;
    }

    public PatientResponseDTO createPatient(PatientResponseDTO patientRequestDTO){

    }


}
