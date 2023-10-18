import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class AuthorizationTests {
    private final static ThreadLocal<String> tokenAdmin = new ThreadLocal<>();
    private final static ThreadLocal<String> tokenUser = new ThreadLocal<>();

    @BeforeTest
    public void preconditions() throws JsonProcessingException {
        baseURI = "http://node.twnsnd.online:31035";
    }

    //  @Test(priority = 1)
    public void createNewAccountTest() {
        String endpoint = "/api/auth/registration";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("CreateNewAccount"))
                .post(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(201);
        tokenUser.set(JsonPath.from(response.getBody().asString()).get("value"));
    }

    // @Test(priority = 2)
    public void loginSuperAdminTest() {
        String endpoint = "/api/auth/login";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("Platforms/SuperAdminCreds"))
                .post(endpoint);
        response.then().assertThat().statusCode(201);
        tokenAdmin.set(JsonPath.from(response.getBody().asString()).get("value"));
    }

    @Test(priority = 3)
    public void loginUserTest() {
        String endpoint = "/api/auth/login";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("UserCreds"))
                .post(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(201);
        tokenUser.set(JsonPath.from(response.getBody().asString()).get("value"));
    }

    //Не работает запрос
    // @Test(priority = 4)
    public void refreshTokenTest() {
        String endpoint = "/api/auth/refresh";
        Response response = given()
                //.header("Content-Type", "application/json")
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("RefreshTokenSchema")));

    }

    public String getJsonData(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("files/" + fileName + ".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
