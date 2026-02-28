package com.pm.patient_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pm.patient_service.model.Patient;

@Repository // tells spring that this is a jpa repostiory
// extends the JPA repo and we pass in the entity that it wants to control
// patient repo gets the patient entity. We also pass in the type of its ID, which is a UUID
// extending gives us crud functionalities out of the box
public interface PatientRepository extends JpaRepository<Patient, UUID> {
// define custom methods to expose to the patient service
    boolean existsByEmail(String email); //exposing this method - JPA handles this behind the scenes
    boolean existsByEmailAndIdNot(String email, UUID id );
}
