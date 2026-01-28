package com.pm.patient_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // notifies springboot that this is the entry point of the application
public class PatientServiceApplication {

	//entry point for our java application
	public static void main(String[] args) {
		// start up the springboot app anytime the project is run
		SpringApplication.run(PatientServiceApplication.class, args);
	}

}
