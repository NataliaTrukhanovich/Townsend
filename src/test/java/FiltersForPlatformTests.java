import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.filters.Filter;
import entity.filters.FilterGroup;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j;
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

@Log4j
public class FiltersForPlatformTests {
    private final static ThreadLocal<String> token = new ThreadLocal<>();
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayList<String> filterIdList = new ArrayList<>();
    ArrayList<String> filterGroupIdList = new ArrayList<>();

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

    //@Test(priority = 1)
    public void topPriorityFiltersTest() {
        String endpoint = "/api/filter-for-platforms/top-priority";
        Response response = given().get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("TopPriorityFiltersSchema")));
    }

    @Test(priority = 2)
    public void getAllFiltersByAdmin() throws JsonProcessingException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ArrayList<String> filterGroupNamesList = new ArrayList<>();
        String endpoint = "/api/filter-for-platforms/admin";
        Response response = given()
                .header("Authorization", "Bearer " + token.get())
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("AllFiltersByGroupsSchema")));

        //Имена и id групп фильтров в ArrayList
        List<FilterGroup> listOfFilterGroups = objectMapper.readValue(response.body().asString(), new TypeReference<>() {});
        for (FilterGroup fG : listOfFilterGroups) {
            filterGroupNamesList.add(fG.getFilterGroupName());
            filterGroupIdList.add(fG.getId());
            for (Filter f : fG.getFiltersForPlatforms()
            ) {
                filterIdList.add(f.getId());
            }
        }
        // filterIdList.forEach(System.out::println);
        filterGroupNamesList.forEach(System.out::println);
    }

   //@Test(priority = 3)
    public void getAllFiltersByUserTest() {
        String endpoint = "/api/filter-for-platforms";
        Response response = given()
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("AllFiltersByGroupsSchema")));
    }

    //@Test(priority = 4)
    public void getFilterByIdTest() {
        String endpoint = "/api/filter-for-platforms/"+"f20052bc-566e-4b21-bc0d-304719d5ed2e";
        Response response = given()
                .header("Authorization", "Bearer " + token.get())
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("FilterForPlatformByIdSchema")));
    }


   // @Test(priority = 5)
    public void createFilterTest() {
        String endpoint = "/api/filter-for-platforms";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .body(getJsonData("CreateNewFilter"))
                .post(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(201);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("CreateNewFilterSchema")));
    }

   // @Test(priority = 6)
    public void updateFilterTest() {
        String endpoint = "/api/filter-for-platforms/" + "1f37e1be-f0f3-4b7f-88a1-c915a4a2cda4";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .body(getJsonData("UpdateFilter"))
                .put(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("UpdateFilterSchema")));
    }

    //@Test(priority = 6)
    public void deleteFilterTest() {
        String endpoint;
        endpoint = "/api/filter-for-platforms/" + "37ad943d-3a1f-422e-9c98-a94748d59997";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .delete(endpoint);
        response.then().assertThat().statusCode(200);
    }

    //Filter Group Tests

    @Test(priority = 7)
    public void getAllFilterGroupsTest() {
        String endpoint = "/api/filter-for-platforms/category";
        Response response = given()
                .header("Authorization", "Bearer " + token.get())
                .get(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("AllFilterGroupsSchema")));
    }

   //@Test(priority = 8)
    public void createFilterGroup() {
        String endpoint = "/api/filter-for-platforms/category";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .body(getJsonData("CreateNewFilterGroup"))
                .post(endpoint);
        response.prettyPrint();
    }

    //@Test(priority = 9)
    public void updateFilterGroupTest() {
        String endpoint = "/api/filter-for-platforms/category/" + "7d80002d-1e1e-4e24-b91e-28a963faa266";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .body(getJsonData("UpdateFilterGroup"))
                .put(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(204);
    }

   // @Test(priority = 10)
    public void deleteFilterGroupTest() {
        String endpoint;
        endpoint = "/api/filter-for-platforms/category/" + "ae1fd4c8-ec0d-43b0-bdc2-938bdc7870bc";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.get())
                .delete(endpoint);
        response.then().assertThat().statusCode(200);
 }

    public String getJsonData(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("files/FiltersForPlatform/" + fileName + ".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
