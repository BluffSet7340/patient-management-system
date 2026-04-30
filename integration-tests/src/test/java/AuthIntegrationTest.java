
// all auth related integration tests

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.notNullValue;

// simulation of a frontend client trying to access these resources
public class AuthIntegrationTest {
// specify base url of all the tests
    @BeforeAll // signal that this should be executed before all tests in the class
    static void setUp() {
        RestAssured.baseURI = "http://localhost:5004"; // address of api-gateway
    }

    // happy path - when things go your way
    // specific naming convention used in naming tests
    @Test
    public void shouldReturnOKWithValidToken() {
        // 1. arrange - add necessary data to prepare for the test
        // 2. act on the test - run the test, eg. calling a login endpoint
        // 3. assert - check if response has a valid token and an OK status
        String loginPayload = """
                    {
                        "email": "test@email.com",
                        "password": "password123"
                    }
                """;
     
        Response response = given()
            .contentType("application/json")
            .body(loginPayload)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .body("token", notNullValue()) // asserts that we have a token that is not null
            .extract() // allows you to extract values from the response
            .response();

        System.out.println("Generated token: " + response.jsonPath().getString("token"));
    }

    // unhappy path - when wrong data is sent
    @Test
    public void shouldReturnUnauthorizedOnIncorrectLoginDetails() {
        // 1. arrange - add necessary data to prepare for the test
        // 2. act on the test - run the test, eg. calling a login endpoint
        // 3. assert - check if response has a valid token and an OK status
        String loginPayload = """
                    {
                        "email": "test@email.com",
                        "password": "password"
                    }
                """;
     
        // not using the response variable to just start the request
        given()
            .contentType("application/json")
            .body(loginPayload)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(401);
    }

}
