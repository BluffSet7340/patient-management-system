package com.pm.analytics_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import patient.events.PatientEvent;

@Service // automatically starts when the analytics-service starts?
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    
    @KafkaListener(topics = "patient", groupId = "analytics-service") // handles events sent to the patient topic, groupId tells the kafka broker who this consumer is   
    public void consumeEvent(byte[] event) {
        // PatientEvent patientEvent = PatientEvent.parse(event);
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event); // create patientEvent object from the byte array
            // perform business related logic
            log.info("Received Patient Event: [PatientId={}, PatientName={}, PatientEmail={}]", patientEvent.getPatientId(), patientEvent.getName(), patientEvent.getEmail());

        } catch (Exception e) {
            // handle exception in trying to consume patient event
            log.error("Error deserializaing event {}", e.getMessage());
            
        }
         

    }
}
