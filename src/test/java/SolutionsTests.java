import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class SolutionsTests {
    private final static ThreadLocal<String> token = new ThreadLocal<>();
    @BeforeTest
    public void preconditions() {
        baseURI = "http://node.twnsnd.online:31035";
        String endpoint = "/api/auth/login";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("SuperAdminCreds"))
                .post(endpoint);
        token.set(JsonPath.from(response.getBody().asString()).get("value"));
    }

  //  @Test
    public void createSolution(){
        String endpoint = "/api/solutions";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .queryParam("platformId", "dc4ec117-4fb6-4447-9406-772585ab41e4")
                .body(getJsonData("CreateNewSolution"))
                .post(endpoint);
        response.prettyPrint();
       // response.then().assertThat().statusCode(201);
        //response.then().assertThat().body(matchesJsonSchema(getJsonData("CreateNewFilterSchema")));

    }
    @Test
    public void getAllSolutionsTest(){
        String endpoint = "/api/solutions";
        Response response = given()
                .header("Authorization", "Bearer " + token.get())
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
       // response.then().assertThat().body(matchesJsonSchema(getJsonData("PlatformsSchema")));

    }
    public String getJsonData(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("files/Solutions/" + fileName + ".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
