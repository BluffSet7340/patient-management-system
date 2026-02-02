package com.pm.patient_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/patients") // handles all requests starting with "patient"
public class PatientController {
    // private, can only be accessed within its own class only
    // and final means that once the reference is init, it cannot be changed or reassigned
    private final PatientService patientService;

    // constructor, another case of dependency injection
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // returns a response entity with a list of patient dtos
    // creates the http response for us
    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getPatients(){
        List<PatientResponseDTO> patients = patientService.getPatients();

        // the .ok means it was successful, so status code 200
        return ResponseEntity.ok().body(patients);
    }

    // The valid annotation does automatic validation on the incoming object,
    // we already applied the validation annotations for the specific fields in 
    // the PatientRequestDTO class
    // @RequestBody tells SpringBoot to take the incoming JSON object and convert it to a a
    // Java object of type PatientRequestDTO 
    @PostMapping()
    public ResponseEntity<PatientResponseDTO> addPatient(@Valid @RequestBody
        PatientRequestDTO patientRequestDTO){
            // we call the createpatient passing in the requestdto and we save a patient to 
            // the db and we return the patient responsedto to the client
            PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);

            // this is what we return to the client, as sort of a confirmation
            return ResponseEntity.ok().body(patientResponseDTO);
    }
    

}
