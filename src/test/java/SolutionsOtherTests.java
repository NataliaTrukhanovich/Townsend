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
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class SolutionsOtherTests {
    private final static ThreadLocal<String> tokenAdmin = new ThreadLocal<>();
    private final static ThreadLocal<String> tokenUser = new ThreadLocal<>();
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayList<String> solutionIdList = new ArrayList<>();

    @BeforeTest
    public void preconditions() {
        baseURI = "http://node.twnsnd.online:31035";
        String endpoint = "/api/auth/login";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("SuperAdminCreds"))
                .post(endpoint);
        tokenAdmin.set(JsonPath.from(response.getBody().asString()).get("value"));
        System.out.println(tokenAdmin.get());
        //  getPlatformIdList();
        response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("UserCreds"))
                .post(endpoint);
        tokenUser.set(JsonPath.from(response.getBody().asString()).get("value"));
    }

    @Test(priority = 1)
    public void getAllSolutionsTest() {
        String endpoint = "/api/solutions";
        Response response = given()
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        // response.then().assertThat().body(matchesJsonSchema(getJsonData("PlatformsSchema")));

    }

    // @Test(priority = 2)
    public void addSolutionInViewed() {
        //String id = platformIdList.get(3);
        String endpoint = "/api/solutions/" + "42d74221-63da-49aa-9fb3-81df6e462e63" + "/viewed";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .put(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        // response.then().assertThat().body(matchesJsonSchema(getJsonData("AddInViewedSchema")));
    }

    //   @Test(priority = 3)
    public void removeAllSolutionsFromViewedTest() {
        String endpoint = "/api/solutions/viewed";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .delete(endpoint);
        response.then().assertThat().statusCode(204);
    }

    //@Test(priority = 4)
    public void removeSolutionByIdFromViewedTest() {
        String endpoint = "/api/solutions/" + "42d74221-63da-49aa-9fb3-81df6e462e63" + "/viewed";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .delete(endpoint);
        response.then().assertThat().statusCode(204);
    }

    @Test(priority = 10)
    public void getAllFiltersOfSolutionTest() {
        String endpoint = "/api/solutions/" + "42d74221-63da-49aa-9fb3-81df6e462e63" + "/filters";
        Response response = given()
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("AllFiltersOfSolutionSchema")));
    }

    @Test(priority = 9)
    public void updateAssociationsBetweenFiltersAndPlatformTest() {
        String endpoint = "/api/solutions/" + "42d74221-63da-49aa-9fb3-81df6e462e63" + "/filters";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .body(getJsonData("FilterItemId"))
                .put(endpoint);
        response.prettyPrint();
    }

    private void getSolutionIdList() throws JsonProcessingException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String endpoint = "/api/solutions";
        Response response = given()
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .get(endpoint);
        response.prettyPrint();
//        List<Platform> listOfPlatforms = objectMapper.readValue(response.body().asString(), new TypeReference<>() {
//        });
//        for (Platform p : listOfPlatforms) {
//            solutionIdList.add(p.getId());
//        }
//        System.out.println("Solutions ids:");
//        solutionIdList.forEach(System.out::println);
    }

    public String getJsonData(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("files/Solutions/" + fileName + ".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
