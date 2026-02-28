# step to build the code
FROM maven:3.9.9-eclipse-temurin-21 AS builder
# specify working directory for code and config...
WORKDIR /app
# copy the dependencies of this file into the root directory, the app directory
COPY pom.xml .
# mvn to install dependencies and cache them - for quicker startups
RUN mvn dependency:go-offline -B
# copy source code into the src folder inside the app directory
COPY src ./src
# create a new package from our SpringBoot app
RUN mvn clean package
# use openjdk runtime (not supported anymore) to run the built code
FROM eclipse-temurin:21-jdk AS runner
# define the working directory again
WORKDIR /app
# copy the snapshop of the patient-service jar that was built in the preceding steps and place it at the specified location
COPY --from=builder ./app/target/patient-service-0.0.1-SNAPSHOT.jar ./app.jar
# specify the port that the app runs on
EXPOSE 5000
# specify command to start code
ENTRYPOINT [ "java", "-jar", "app.jar" ]