package entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;

import java.util.ArrayList;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class PlatformOtherTests {
    private final static ThreadLocal<String> token = new ThreadLocal<>();
    private static String userId;
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayList<String> platformIdList = new ArrayList<>();

    @BeforeTest
    public void preconditions() throws JsonProcessingException {
        baseURI = "http://node.twnsnd.online:31035";
        String endpoint = "/api/auth/login";
        Response response = given()
                .header("Content-Type", "application/json")
                //.body(getJsonData("SuperAdminCreds"))
                .post(endpoint);
        token.set(JsonPath.from(response.getBody().asString()).get("value"));
      //  getPlatformIdList();
     //   getUserId();
    }
}
