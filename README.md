Following this [tutorial](https://www.youtube.com/watch?v=tseqdcFfTUY&list=PLwnoReiC-egEJ-cpIExHV73LZm1yiRN2O&index=3) to deepen my understanding on SpringBoot.

## Daily Journal

### 21st January 2026

Back at it again with another project, following this [tutorial](https://www.youtube.com/watch?v=tseqdcFfTUY&list=PLwnoReiC-egEJ-cpIExHV73LZm1yiRN2O&index=3) where I learn to build a patient management system using microservices. Oracle JDK is required but I already have that installed so gg. Docker is installed and IDE I'm using Cursor

I am using the Spring Boot Architecture to create a restful API and microservice. The SpringBoot API is divided into three components: controller that handles which request does what and goes where, the service which handles the business logic, perhaps updating the db and ensuring values are unique where they have to be, and the repository which interacts with the database using raw sql queries.

DTO stands for Data Transfer Object -  a JavaScript Object. We use that when we want to add new patients to our database with certain attributes. This keeps our actual entities, or tables private. We can validate the fields in the DTO and we can also send and receive only the fields that are relevant to the client

Now I'm gonna create our first microservice, the patient microservice using SpringBoot

Since I'm not using the IntelliJ IDE, I'll be using start.spring.io website to generate the SpringBoot project for me. Since SpringBoot is modular, we only select the exact packages we need to keep the project as small as possible, suitable for microservices

Dependencies used - SprinBoot Web (create Restful APIs), Spring Data JPA (ensures persistence to database of entities via behind the scene queries), DevTools (application restarts, review changes quickly), Postgress SQL Driver (allows repo layer to interact with postgress db), and Validation (validate request received from the client, validate DTOs and entities before being saved to the DB).

This is the patient service so we are going to create a patient entity for it. So I have to create a package new called model, this is where the Patient will be created. 

### 24nd January 2026

Still working on defining the patient entity. I generated getters and setters via source actions. JPA is helpful since it will see the patient entity that does not exist in the database and add it to the db for us, removing the need for manual entry.

SpringBoot also provides an in memory db where the ram is used to store the db of the laptop, allowing for fast read and write access. Useful for us to quickly check if our patient microservice is working as expected. I had to add the dependency for the in memory database. I reloaded the developer window so that Maven realizes. Having issues with the h2database dependency maven is not installing it. Okay I went to the [docs](https://mvnrepository.com/artifact/com.h2database/h2/2.4.240) and copied the dependency code for the latest version and then ran the mvn clean install and it ran successfully. So I installed some spring boot extensions so that I can get auto-complete features on the application.properties file. 

The next issue is with the application.properties file. It says that spring.h2.database is unknown so need to figure that out. So I figured that out thanks to [stack overflow](https://stackoverflow.com/questions/70926463/cannot-resolve-configuration-property-spring-h2-console-enabled). I am using SpringBoot 4.0.2 so I copied the code for the dependency and pasted that in to my pom.xml and rebuilt the project and boom now it is working.

Adding configuration for the spring database. Added more config lines to the application.properties file. So I started the SpringBoot application but unable to access my h2-console, I get a 404 error. So it cannot find it but it should be there so I got to debug this. Tried adding a starter web dependency but still not working. 

### 25th January 2026

Still trying to figure out the Whitelabel Error Page. So I just used Cursor AI and it told me that due to a trailing whitespace on the /h2-console path in my applcation.properties file. It is working now. I copied the data.sql file from the youtuber's repo. The point of this data.sql file is to populate the database with dummy data for the testing phase.  

I logged in to the h2-console and I can see the Patient entity with all the expected fields - it's ready for being updated with patients and more. 

Okay the way the sql statement works is that it insert into the patient entity via select statement. The select statement has the information of the patient to be inserted depending on the evaluation of the where not exist clause. If that patient does not exist then create this patient with details outlined in the select statement.

I logged in to the h2-console and am able to see all the dummy data as well. Suitable for local testing.

### 26th January 2026

The in memory database is setup. The next step for me is to create the repo layer - interacts with the db to perform operations on the db. JPA and repo layer work together. 

The java interface consists of methods and arguments but no bodies. The body will have to be implemented by whichever class decided to inherit from that interface. So I am making a PatientRepository interface.

The repository package is created and now I created the service package, the last piece to handle business logic.

For the PatientService I instantiate the class via a dependency injection instead of using the new keyword to instantiate the class - apparently this approach makes the code more modular. 

JPA provides the findAll and many other methods by default. I created the DTO package. 

### 27th January 2026 -

We use DTOs to hide the internal database structure. I created a mapper class that converts the patient entity to a patient DTO.

The mapper class is complete and the Patient service returns the list of patients as DTOs. The last step to implement in the controller to handle the http requests. 

### 28th January 2026 - 

ResponseEntity represents the whole HTTP response: status code, headers, and body. I created the api-request folder to test the endpoints for the different microservices. 

I installed the rest client extension so that I can run http requests. There is an error with the get request, "could not prepare statement registration date not found". Since that attribute is not added to the patient dto, need to figure out where it is going wrong. 

Okay I found the bug, was trying to be smart by having different variable names for the registered date column but there is a non match between the data.sql file and the Patient entity so I'll fix that. The error has been fixed and the get request for all patients is working now.

The response entity takes the DTO and converts that to JSON. We send and take request via DTOs. The patient response DTO has no validation but the request requires validation such as no whitespace or empty characters and max limit of 100 characters and more. Ideally the data that get input vs. gets returned should be thought out carefully.

Next step is to take the DTO, accept and covert it to a domain entity model and call the patient repository to add the patient to the db. In the patient response dto we return the id but in the request counterpart there is no id, perhaps generated by uuid function?