# Patient Management System

A tool for administrators to manage the patients stored in their Database. SpringBoot is used for the backend, Kafka for real-time messaging and consumption between services, gRPC is used for real-time communication between services, AWS for cloud deployment, and Docker for containerization. This project was done to deepen my understanding of SpringBoot and served as an introduction to microservices architecture.

## Technologies

- SpringBoot
- Docker
- Kafka
- gRPC
- AWS
- REST Assured

## Features

The administrator can log in and modify the patient database in three ways: add, update, and delete patients. Each time a new patient is added, the billing service creates a billing account for that patient and Kafka sends a patient created event to the analytics service to observe changes in the system. All services were containerized using Docker and tested on the internal network to simulate a microservices architecture. Lastly, the api gateway service ensures that only the admin with a valid JWT can access sensitive operations of other services. 

## The Process

I followed this [tutorial](https://www.youtube.com/watch?v=tseqdcFfTUY&list=PLwnoReiC-egEJ-cpIExHV73LZm1yiRN2O&index=3) to deepen my understanding on SpringBoot.

I commented almost each part of the code since this was all new to me until I got an understanding of the repeated patterns used in SpringBoot. I also made a daily journal entry to document what I learnt and how I squashed bugs that arose due to the tutorial being more than a year old. 

## What I Learned

The most important lessons were the foundational lessons. Here is the list below - 

- When a client interacts with a database in SpringBoot, they should do so using Data Transfer Objects, or DTOs. A developer hide away the details of the internal objects in the database and can pick and choose which fields can be exposed to the client.

- To create methods that interact with a database, create a service folder and create a Java file that appends the name and "Service" together. An example of this is the PatientService.java file. 

- Dependency injection works by creating a constructor for a class where the arguments constitute the dependencies required by that class to function. An example of this is the PatientService that requires PatientRepository, the BillingServiceGrpcClient, and the KafkaProducer as its dependencies. 

- For testing different services together, create a Dockerfile with the necessary environment variables and arguments and put them all on the same network such as "internal". This allows you to simulate the services communication with each other in a microservices architecture pattern.

- For services that have a database a repository class file can be created and extended using JPA repository. This gives you basic CRUD functionality out of the box. You can create your own queries using the English language instead of sql statements. 

- The model folder stores the properties of the entity that you want to model. 

- gRPC, created by Google is preferred for communication between internal services due to it higher speed. 

- Despite the tutorial being just one year old, a lot of the bug squashing came down to changes being made to core technologies. An example of this is having to a different image of Kafka, namely Apache Kafka. Another example is the fact that the author uses SpringBoot version and I am using SpringBoot version 4, causing compatibility issues when trying to configure the api gateway service 

## Future Work (Improvements)

Below are some of the features that could be implemented to further enhance the experience of the hospital administrator - 

- [ ] Allow a user with admin to add or remove patient from database
- [ ] Add Integration test for the above function
- [ ] Flow Diagrams to display how services interact with each other
- [ ] Add events such as update, delete patient for analytics-service to track via kafka 

## Running the Project

- **Prerequisites:** Java 21+

- **Run with Docker (example for `patient-service`):**
	- `docker build -t patient-service ./patient-service`
	- `docker run -p 8080:8080 patient-service`  (check the service's `application.yml`/`application.properties` for the actual port)

- **Integration tests:** `cd integration-tests && ./mvnw test` (or `mvnw.cmd test` on Windows)

- **Notes:** Many services require environment variables (DB, Kafka, gRPC endpoints). Check each service's `src/main/resources` for required settings before running.

## Video Demo

This will be uploaded soon once I complete the frontend.
