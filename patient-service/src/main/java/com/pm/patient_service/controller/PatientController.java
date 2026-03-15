package com.pm.patient_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.dto.validators.CreatePatientValidationGroup;
import com.pm.patient_service.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/patients") // handles all requests starting with "patient"
@Tag(name = "Patient", description = "API for managing patients") // tag controller for open api
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
    @Operation(summary = "Get all Patients" ) // open ai knows that this is a get request, more options can be added
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
    // runs the default validation properties for the patientresponsedto and also the extra validation properties for the PatientValidationGroup class
    @PostMapping()
    @Operation(summary = "Create a new patient" )
    public ResponseEntity<PatientResponseDTO> addPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody
        PatientRequestDTO patientRequestDTO){
            // we call the createpatient passing in the requestdto and we save a patient to 
            // the db and we return the patient responsedto to the client
            PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);

            // this is what we return to the client, as sort of a confirmation
            return ResponseEntity.ok().body(patientResponseDTO);
    }
    
    // update patient, use the put request, convert the uuid to the id variable
    // use the path variable to create a mapping between the id variable in the PutMapping
    // so that you can access it as an argument in the updatePatient
    
    // the request body means that the json that we get to update the patient, will be assigned to the patient request dto
    
    // the validated tag refers to the patient request dto in the request body, validate requests using all default specified in that dto specifially 
    @PutMapping("/{id}")
    @Operation(summary = "Update a Patient" )
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id, @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO){

        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientRequestDTO);

        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Patient" )
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id){
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build(); // status code of 204 - no content
    }
}
