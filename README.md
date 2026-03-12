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

### 29th January 2026 - 

Working on the PatientRequestDTO file and used the LocalDate.parse() on the string date to ensure compatibility with the patient entity. Then used the UUID.randomuuid() command to generate id for a patient. The DTOs are complete and the next step is to add the post mapping for adding a new patient. Now I am creating a post request inside of the api folder and I want to see if the order of key value pairs matter when sending the request. 

Just learnt that there should be an empty line break, just one line between the header and body, otherwise you get an error that says header name must be valid HTTP token.

Okay API post call failed since I used the wrong attribute name inside of the request body, it should be "number" not "phone" to make it compatible with PatientRequestDTO class. 

My patient response dto is a little different, in that it returns the phone number but it is null in the response after the post request, gotta check that out. Well based on his spec, there is no need to return the phone number, I'll follow his spec. 

Me adding the phone number was a mistake, there is no such thing on the data.sql file, it was never meant to be. 

### 31st January 2026 - 

The adding of a new patient works and the validation also works, for example sending email as an empty string triggers a email is required error, a bad request.

### 1st February 2026 - 

We need to handle the error responses to not reveal too much information about the backend being used.

I created a GlobalExceptionHandler class to centralize any and all errors that might arise from the service, repo, controller, and more. This gives me control over what info I reveal to the client or the frontend developer (I suppose?), minimizing security risk. In my case, this will handle the exception caused from bad requests such as missing fields or exceeding the max character limit. 

### 2nd January 2026 - 

Had to move my project to another laptop running linux since the one running windows kept throwing kernel errors. Just spent the time configuring, debugging, cleanning and installing packages using Maven. 

Business logic is a set of tasks and processes that follow constraints to achieve a business goal. One example is that an email address must be unique as having the same email address attributed to multiple people causes issues with contacting the correct patient. I will implement this business logic right now 

### 5th January 2026 -

Currently working on implementing an error message for when there is a user with an existing email address when trying to add a new patient. I have to install the intellisense so that it is easier for me create the method for seeing if a patient exists by email id. Installing some extensions for this. 

Added the method to the patient repo and now I'll use that in my patien service.

So I had to define a new exception class for my email already exists exception. 

### 12th February 2026 - 

Now trying to figure out the issues with the log not being resolved, since I need to be able to write custom logs for specific errors, such as an email address not being unique. I am using the logger from the sl4j library since it is the industry standard used in production grade applications. 

Okay the message defined in the log is what is returned to client once the request is made and the other messages are seen in the console itself. Since the application.properties file was modified, the full error message outline in the createPatient is seen on the console. 

There are two messages seen on the console, the first message is just the exception itself and the second message outlines what exception it belongs to, so the EmailAlreadyExistsException

The next method to implement is to update a patient. Now I have to create a PatientNotFoundException for when it cannot find the patient by specified id.

Had to do very similar code to the email already exists exception for the patient not found exception.

### 13th February 2026 - 

The endpoint for the update patient is done and now I should be able to update a patient via its ID. 

What is cool that after updating the patient details when you call the .save() method JPA understand that an existing patient is being updated, since it know that this patient exists and has the matching ID. 

I think if you do not add all the fields, whether they're being updated or not, the transaction fails and you get internal server error. The patient request dto already has a the @Not blank validation but the controller needs to enforce it?

Adding the validation check and now I get better errors that are more readable. Still getting the registration email is missing error but that should not be updated so we use validation groups, adding custom validation stuff for each of the properties I guess. 

NotBlank does not allow empty content, whether that be null or empty spaces. 

NotNull does not allow null values but the fields can be empty?

### 15th February 2026 -

Valid is the standard Java annotation and Validated is the Spring specific annotation. 

Once a successful put request is made, SpringBoot returns the patient repose DTO. 

### 26th February 2026 -

Leaving all else the same and trying to update the patient name gives me an email already exists error. Issue fixed by removing the code that checks if an email address already exists in the update patient function.

The correct fix was to add a boolean in the patient repo interface that checks if any patient except for the one that it just found has the same email id. JPA handles this for us?

Derived query methods are cool - spring figures out the query under the hood based on the naming convention used.

Onwards to the delete patient endpoint. The controller returns a void so you receive a 204 no content response after deleting a patient. 

The patient service restarts with the same fresh copy of the data.sql file so changes are not persistent. 

Onto using swagger / openai to create api documentation. SpringBoot has a package to create such documentation

### 27th February 2026 - 

Viewed the swagger docs through the editor. 

### 28th February 2026 -

Used this [tutorial](https://learn.microsoft.com/en-us/visualstudio/docker/tutorials/docker-tutorial?WT.mc_id=vscode_docker_aka_getstartedwithdocker) to get started with docker on vscode 

PS does not like back slashes but is okay with back ticks

Used Claude to help me create the docker command.

Squashed some bugs with the help of Claude when running the docker command.

Created Docker to emulate a producion environment.

Dockerfile - instructions to create docker image

Now add some config to the dockerfile and build an image

Commented out the config inside the application.properties file since we using Docker now

Openjdk is deprecated and need to use alternatives - suggested by Claude

Port issues with Docker - redoing it again

Some port issues since postgres by default uses port 5432.

Had lots of issues with running both the patient-service and patient-service-db containers due to port issues, file path issues and not waiting long enough for the container to init and to be ready for a connection

### 3rd March 2026 - 

Any changes to code, restart the container and get it running. Need to install extensions so that I can create a connection to my database. I used SQLTools Extension and the postgresql driver and somehow it worked.

Right clicked on the table in my connections and I can see my records alhamdulillah. The Get request is working. 

gRPC is a super fast protocol built on HTTP 2 used for efficient communication between microservices, similar to how REST is used for communication between the frontend and the backend. Instead of JSON, we use Protobuf for rGPC.

The .proto file is used to define the data structure of the object you're working with. Then the code for the getters and setters are automatically generated. We describe the grpc server with this - what methods and responses will look like. If any changes are made, all microservices can adapt by regenerating the code again. Great for adding more microservices. Proto file can be stored as maven package or stored in the cloud. In my case I'll just copy the .proto file to all microservices

A billing service will be created next.

Okay I need to figure out how I'll add a module of type springboot in vscode. A new module is akin to creating a new microservice. In my case I just created a new Java project with the necessary config using only SpringBoot web as my dependecy. Now have to add depedency for protobuff and grpc. Need to add some build steps to generate the protobuff code anytime the app is started

### 4th March 2026 - 

Created my first protofile. Use the dependency in the pom file to generate the grpc code based on the protofile defined. This is done by using the Maven compile command for the billing-service. The generated code is found under the protobuf folder under the target folder. A stub has been created called BillingServiceGrpc.java. Under the Java folder, we have files for the request and response, generated based on the protofile, similar to a DTO. 

Config the grpc server to start whenever SpringBoot starts

### 7th March 2026 - 

Testing the gRPC service. The service is now working according to the logs. Next is to make the request to create a patient's billing account. Uses http 2 under the hood so easy to test the server. 

The first line of req is broken down into - protocol address and port/name of service/name of method of service. Still issues with the grpc service.

I could not get it to work inside of vscode so I used postman and it works, I got the response with the account id and the status.

Onto dockerizing billing service.

### 11th March 2026 - 

The protofile has to be copied for each service, in production it'd be located in a central location. 

### 12th March 2026 -

Claude Code helped me with debugging the version conflicts of google's protobuf and the os-maven-plugin. Running mvn clean compile to install necessary packages. Still debugging issues with the grpc service not being imported into the grpc package of patient-service. Okay I was able to fix the issue now. Claude suggested that the issue lied with VsCode and told me clear the Java serve workspace that seems to have resolved the issue.  
