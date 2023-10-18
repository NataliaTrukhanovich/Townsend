import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Platform;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class PlatformReviewsTests {
    private final static ThreadLocal<String> token = new ThreadLocal<>();
    ObjectMapper objectMapper = new ObjectMapper();
    List<Platform> listOfPlatforms = new ArrayList<>();
    ArrayList<String> platformIdList = new ArrayList<>();

    @BeforeTest
    public void preconditions() throws JsonProcessingException {
        baseURI = "http://node.twnsnd.online:31035";
        String endpoint = "/api/auth/login";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("SuperAdminCreds"))
                .post(endpoint);
        token.set(JsonPath.from(response.getBody().asString()).get("value"));
        getPlatformIdList();
    }

    @Test
    public void createReviewTest(){
        String endpoint = "/api/platform-review";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .queryParam("id", platformIdList.get(3))
                .body(getJsonData("CreateReview"))
                .post(endpoint);
        response.prettyPrint();
    }

    @Test
    public void deleteReviewTest(){
        String endpoint = "/api/platform-review";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .queryParam("id", platformIdList.get(3))
                .body(getJsonData("CreateReview"))
                .delete(endpoint);
        response.prettyPrint();
    }

    public String getJsonData(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("files/Platforms/" + fileName + ".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void getPlatformIdList() throws JsonProcessingException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String endpoint = "/api/platforms";
        Response response = given()
                .header("Authorization", "Bearer " + token.get())
                .get(endpoint);
        response.prettyPrint();
        List<Platform> listOfPlatforms = objectMapper.readValue(response.body().asString(), new TypeReference<>() {
        });
        for (Platform p : listOfPlatforms) {
            platformIdList.add(p.getId());
        }
        System.out.println("Platforms ids:");
        platformIdList.forEach(System.out::println);
    }
}
