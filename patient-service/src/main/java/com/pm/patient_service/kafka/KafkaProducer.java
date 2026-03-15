package com.pm.patient_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pm.patient_service.model.Patient;

import patient.events.PatientEvent;

// use kafka template
@Service
public class KafkaProducer {
    // define msg types and send msgs
    // kafka event of the form String as key and byte[] as value
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate; // autowire template and assign to instance - dependency injection
    }

    // create and send an event
    public void sendEvent(Patient patient){
        // event type describes particular event inside of topic
        PatientEvent patientEvent = PatientEvent.newBuilder()
        .setEmail(patient.getEmail()).
        setName(patient.getName()).
        setPatientId(patient.getId().toString()).
        setEventType("PATIENT_CREATED")
        .build();

        // send event using kafka template
        try {
            // second argument converts the patientEvent instance to a byte array - smaller in size and can be converted to object code that can be used in consumer code
            kafkaTemplate.send("patient", patientEvent.toByteArray());
        } catch (Exception e) {
            log.error("Error sending PATIENT_CREATED event: {}", patientEvent);
        }
    }

    // define type and properties of the msg we will send - kafka events
}
