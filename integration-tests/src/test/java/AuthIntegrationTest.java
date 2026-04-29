
// all auth related integration tests

import org.junit.jupiter.api.BeforeAll;

import io.restassured.RestAssured;

// simulation of a frontend client trying to access these resources
public class AuthIntegrationTest {
// specify base url of all the tests
    @BeforeAll // signal that this should be executed before all tests in the class
    static void setUp() {
        RestAssured.baseURI = "http://localhost:5004"; // address of api-gateway
    }
}
