package com.pm.patient_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;
import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.exception.EmailAlreadyExistsException;
import com.pm.patient_service.exception.PatientNotFoundException;
import java.time.LocalDate;

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

    // this function takes the patientrequestDTO as an argument and called the mapper function
    // on it to convert the DTO to a domain object
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            // we have to define this custom exception in the globalExceptionHandlerException
            throw new EmailAlreadyExistsException("A patient with this email already exists " +
                patientRequestDTO.getEmail()
            );
        }

        Patient newPatient =  patientRepository.save(PatientMapper.toModel(patientRequestDTO)); // .save() is provided by JPA
                
        // then return the newly added patient as DTO
        return PatientMapper.toDTO(newPatient); 
    }

    // update patient
    // id is how we find the patient and the PatientRequestDTO is what we send to 
    // update the details of a patient
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        // patientRepository gives us all the methods to perform actions on the database
        // optional type error since there could be chance that it may not find the patient and return null
        Patient patient = patientRepository.findById(id).orElseThrow(()-> new PatientNotFoundException("Patient not found with id: " + id));

        // patient is found but we also want to check if the email already exists
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            // we have to define this custom exception in the globalExceptionHandlerException
            throw new EmailAlreadyExistsException("A patient with this email already exists " +
                patientRequestDTO.getEmail()
            );
        }

        // now the patient can be updated
        // use getters and setters to do your updates
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setName(patientRequestDTO.getName());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setEmail(patientRequestDTO.getEmail());

        // reg date cannot be changed but what if the person enters it wrong then it's over

    }

}
