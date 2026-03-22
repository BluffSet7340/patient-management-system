package com.pm.analytics_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class KafkaConsumer {
    @KafkaListener(topics = "patient", groupId = "analytics-service") // handles events sent to the patient topic, groupId tells the kafka broker who this consumer is   
    public void consumeEvent(byte[] event) {
        PatientEvent patientEvent = PatientEvent.parse(event);
         

    }
}
