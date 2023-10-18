import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Platform;
import entity.platformById.PlatformById;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

@Log4j
public class PlatformsCrudTests {

    private final static ThreadLocal<String> token = new ThreadLocal<>();
    ObjectMapper objectMapper = new ObjectMapper();
    List<Platform> listOfPlatforms = new ArrayList<>();
    ArrayList<String> platformIdList = new ArrayList<>();

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

     @Test(priority = 1)
    public void getPlatformsTest() throws JsonProcessingException {
        String endpoint = "/api/platforms";
        Response response = given()
                .header("Authorization", "Bearer " + token.get())
                .get(endpoint);
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("PlatformsSchema")));
        response.prettyPrint();
        listOfPlatforms = objectMapper.readValue(response.body().asString(), new TypeReference<>() {
        });
        for (Platform p : listOfPlatforms) {
            platformIdList.add(p.getId());
        }
        platformIdList.forEach(System.out::println);
    }

    @Test(priority = 2, dataProvider = "Data for searching platform by name")
    public void getPlatformByNameTest(String searchName, String expectedPlatformName) throws JsonProcessingException {
        String endpoint = "/api/platforms";
        Response response = given()
                .header("Authorization", "Bearer " + token.get())
                .formParam("name", searchName)
                .get(endpoint);
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("PlatformsSchema")));
        response.prettyPrint();
        List<Platform> listOfFoundedPlatforms = objectMapper.readValue(response.body().asString(), new TypeReference<>() {
        });
        if (expectedPlatformName.length() != 0) {
            String actualPlatformName = listOfPlatforms.get(0).getName();
            Assert.assertEquals(actualPlatformName, expectedPlatformName);
        } else {
            System.out.println("Number of platforms with searchName = " + searchName + "  -  " + listOfFoundedPlatforms.size());
            for (Platform p : listOfFoundedPlatforms
            ) {
                System.out.println(p.getName() + " Contains " + searchName);
                Assert.assertTrue(p.getName().toLowerCase().contains(searchName.toLowerCase()));
            }
        }
    }


    @Test(priority = 3)
    public void getPlatformByIdTest() throws JsonProcessingException {
        String id = platformIdList.get(0);
        String endpoint = "/api/platforms/" + id;
        Response response = given().get(endpoint);
        response.then().assertThat().statusCode(200);
        response.prettyPrint();
        response.then().assertThat().body(matchesJsonSchema(getJsonData("PlatformByIdSchema")));
        PlatformById platformById = objectMapper.readValue(response.body().asString(), PlatformById.class);
        Assert.assertEquals(platformById.getPlatform().getId(), id);

        response = given().when().get(endpoint + 1);
        response.then().assertThat().statusCode(400);
        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");
    }

    @Test(priority = 4)
    public void createPlatformWithoutFiltersTest() {
        String endpoint = "/api/platforms";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .body(getJsonData("CreateNewPlatform"))
                .post(endpoint);
        response.then().assertThat().statusCode(201);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("NewPlatformSchema")));
        platformIdList.add(response.getBody().jsonPath().getString("id"));
        response.prettyPrint();
    }

    // @Test(priority = 5)
    public void updatePlatformDataByIdTest() {
        String id = platformIdList.get(0);
        String endpoint = "/api/platforms/" + id;
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .body(getJsonData("UpdatePlatform"))
                .patch(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("UpdatePlatformSchema")));

        response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .body(getJsonData("UpdatePlatform"))
                .patch(endpoint + 1);
        response.then().assertThat().statusCode(400);
        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");
    }

   //@Test(priority = 6)
    public void deletePlatformByIdTest() {
        String id = platformIdList.get(0);
        String endpoint = "/api/platforms/" + id;
        Response response = given()
                .header("Authorization", "Bearer " + token.get())
                .delete(endpoint);
        System.out.println(baseURI + endpoint);
        response.then().assertThat().statusCode(200);

        response = given()
                .header("Authorization", "Bearer " + token.get())
                .delete(endpoint + 1);
        response.prettyPrint();
        response.then().assertThat().statusCode(400);
        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");
    }

    public String getJsonData(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("files/Platforms/" + fileName + ".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DataProvider(name = "Data for searching platform by name")
    public Object[][] getDataName() {
        return new Object[][]{
                {listOfPlatforms.get(0).getName(), listOfPlatforms.get(0).getName()},
                {"sto", ""}
        };
    }
}
