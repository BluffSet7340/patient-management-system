import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class PatientIntegrationTest {
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:5004/" ;
    } 

    @Test
    public void shouldReturnAllPatientsWithValidatedToken() {
        String loginPayload = """
                    {
                        "email": "test@email.com",
                        "password": "password123"
                    }
                """;
     
        // request to grab the token from the response 
        String token = given()
            .contentType("application/json")
            .body(loginPayload)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .body("token", notNullValue()) // asserts that we have a token that is not null
            .extract() // allows you to extract values from the response
            .jsonPath()
            .get("token");

        Response response = given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/api/patients")
        .then()
        .statusCode(200)
        .body("patients", notNullValue()) // asserts that body has a patients property that is not not null
        .extract() 
        .response();
        // let's observe the response of the request
        
        System.out.println("Generated response: " + response.jsonPath().prettify()); // gets response as neat json

    }



}
