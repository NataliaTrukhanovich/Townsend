import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class UserTests {
    private final static ThreadLocal<String> tokenUser = new ThreadLocal<>();
    private static String userId;
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayList<String> platformIdList = new ArrayList<>();

    @BeforeTest
    public void preconditions() throws JsonProcessingException {
        baseURI = "http://node.twnsnd.online:31035";
        String endpoint = "/api/auth/login";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("UserCreds"))
                .post(endpoint);
        tokenUser.set(JsonPath.from(response.getBody().asString()).get("value"));
    }

    @Test(priority = 1)
    public void getUserInfo() {
        String endpoint = "/api/users/info";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
    }

    // @Test
    public void getFavoritesPlatforms() {
        String endpoint = "/api/users/platforms-favorites";
        Response response = given()
                .header("Authorization", "Bearer " + tokenUser.get())
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        // response.then().assertThat().body(matchesJsonSchema(getJsonData("FavoriteAndViewedPlatforms")));

    }

    //   @Test(priority = 6)
    public void getViewedPlatforms() {
        String endpoint = "/api/platforms/viewed";
        Response response = given()
                .header("Authorization", "Bearer " + tokenUser.get())
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        // response.then().assertThat().body(matchesJsonSchema(getJsonData("FavoriteAndViewedPlatforms")));

    }

       @Test(priority = 7)
    public void getViewedSolutions() {
        String endpoint = "/api/users/solutions-viewed";
        Response response = given()
                .header("Authorization", "Bearer " + tokenUser.get())
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        // response.then().assertThat().body(matchesJsonSchema(getJsonData("FavoriteAndViewedPlatforms")));

    }
    public String getJsonData(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("files/Users/" + fileName + ".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
