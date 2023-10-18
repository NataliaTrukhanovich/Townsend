import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Platform;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
@Log4j
public class PlatformOtherOperationsTests {
    private final static ThreadLocal<String> tokenAdmin = new ThreadLocal<>();
    private final static ThreadLocal<String> tokenUser = new ThreadLocal<>();
    private static String userId;
    private static String adminId;
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayList<String> platformIdList = new ArrayList<>();

    @BeforeTest
    public void preconditions() throws JsonProcessingException {
        baseURI = "http://node.twnsnd.online:31035";
        String endpoint = "/api/auth/login";
        System.out.println("--------------------------Preconditions----------------------------");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("SuperAdminCreds"))
                .post(endpoint);
        tokenAdmin.set(JsonPath.from(response.getBody().asString()).get("value"));
        System.out.println(tokenAdmin.get());
        getPlatformIdList();
        response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("UserCreds"))
                .post(endpoint);
        tokenUser.set(JsonPath.from(response.getBody().asString()).get("value"));
        getUserId();
        getAdminId();
    }


   // @Test(priority = 1)
    public void addPlatformInFavoritesTest() {
        String id = platformIdList.get(16);
        System.out.println("Platform id = " + id);
        String endpoint = "/api/platforms/" + id + "/favorites";
        String body = "{\n" + "\"userId\": \"" + userId + "\"" + "\n}";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .body(body)
                .put(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("AddInFavoritesSchema")));

//        String id = platformIdList.get(16);
//        System.out.println("Platform id = " + id);
//        String endpoint = "/api/platforms/" + id + "/favorites";
//        String body = "{\n" + "\"userId\": \"" + adminId + "\"" + "\n}";
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + tokenAdmin.get())
//                .body(body)
//                .put(endpoint);
//        response.prettyPrint();
//        response.then().assertThat().statusCode(200);
    //    response.then().assertThat().body(matchesJsonSchema(getJsonData("AddInFavoritesSchema")));

        //Невалидный id платформы
//        endpoint = "/api/platforms/" + id + 1 + "/favorites";
//        response = given()
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + tokenUser.get())
//                .body(body)
//                .put(endpoint);
//        response.then().assertThat().statusCode(400);
//        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");
//
//        //Невалидный id юзера
//        endpoint = "/api/platforms/" + id + "/favorites";
//        body = "{\n" + "\"userId\": \"" + userId + 1 + "\"" + "\n}";
//        response = given()
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + tokenUser.get())
//                .body(body)
//                .put(endpoint);
//        response.then().assertThat().statusCode(400);
//        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");
    }

  //  @Test(priority = 2)
    public void removePlatformFromFavoritesTest() {
        String id = platformIdList.get(1);
        String body = "{\n" + "\"userId\": \"" + userId + "\"" + "\n}";
        String endpoint = "/api/platforms/" + id + "/favorites";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .body(body)
                .delete(endpoint);
        response.then().assertThat().statusCode(200);

        //Невалидный id платформы
        endpoint = "/api/platforms/" + id + 1 + "/favorites";
        response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .body(body)
                .delete(endpoint);
        response.then().assertThat().statusCode(400);
        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");

        //Невалидный id юзера
        endpoint = "/api/platforms/" + id + "/favorites";
        body = "{\n" + "\"userId\": \"" + userId + 1 + "\"" + "\n}";
        response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .body(body)
                .delete(endpoint);
        response.then().assertThat().statusCode(400);
        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");
    }

   // @Test(priority = 3)
    public void addPlatformInViewed() {
        String id = platformIdList.get(3);
        String endpoint = "/api/platforms/" + id + "/viewed";
        String body = "{\n" + "\"userId\": \"" + userId + "\"" + "\n}";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .body(body)
                .put(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("AddInViewedSchema")));

        //Невалидный id платформы
        endpoint = "/api/platforms/" + id + 1 + "/viewed";
        response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .body(body)
                .put(endpoint);
        response.then().assertThat().statusCode(400);
        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");

        //Невалидный id юзера
        endpoint = "/api/platforms/" + id + "/viewed";
        body = "{\n" + "\"userId\": \"" + userId + 1 + "\"" + "\n}";
        response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .body(body)
                .put(endpoint);
        response.then().assertThat().statusCode(400);
        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");
    }

   // @Test(priority = 4)
    public void removeAllPlatformsFromViewedTest(){
        String endpoint = "/api/platforms/viewed";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .delete(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(204);
    }

    @Test(priority = 5)
    public void removePlatformByIdFromViewedTest() {
        String endpoint = "/api/platforms/" + "1b10c8e4-4e7a-485e-ad06-dc885afcff61" + "/viewed";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenUser.get())
                .delete(endpoint);
        response.then().assertThat().statusCode(204);
    }

    //@Test(priority = 6, dataProvider = "Data for sorting")
    public void sortingPlatformsTest(String s, String sort) throws JsonProcessingException {
        String endpoint = "/api/platforms/filtration";
        Response response = given()
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .header("Content-Type", "application/json")
                .queryParam("type", sort)
                .post(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(201);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("SortedPlatformsSchema")));
        List<Platform> listOfPlatforms = objectMapper.readValue(response.body().asString(), new TypeReference<>() {
        });
        ArrayList<String> listOfNamesOfPlatforms = new ArrayList<>();
        for (Platform p : listOfPlatforms
        ) {
            listOfNamesOfPlatforms.add(p.getName());
        }
        List<String> sortedlistOfNamesOfPlatforms = new ArrayList<>();
        if (sort.equals("asc")) {
            sortedlistOfNamesOfPlatforms = listOfNamesOfPlatforms.stream().sorted().collect(Collectors.toList());
        } else if (sort.equals("desc")) {
            sortedlistOfNamesOfPlatforms = listOfNamesOfPlatforms.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
        }
        for (int i = 0; i < listOfNamesOfPlatforms.size(); i++) {
            Assert.assertEquals(listOfNamesOfPlatforms.get(i), sortedlistOfNamesOfPlatforms.get(i));
            System.out.println(listOfNamesOfPlatforms.get(i) + "   =====   " + sortedlistOfNamesOfPlatforms.get(i));
        }
    }

   // @Test(priority = 7)
    public void getMainOptionsOfPlatform(){
        String id = platformIdList.get(2);
        String endpoint = "/api/platforms/" +id+ "/main-options";
        Response response = given()
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .get(endpoint);
        response.prettyPrint();

    }
    //@Test(priority = 8)
    public void getFiltersOfPlatform(){
        String id = platformIdList.get(3);
        String endpoint = "/api/platforms/"+id+"/filters";
        Response response = given()
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .get(endpoint);
        response.prettyPrint();
        //response.then().assertThat().statusCode(200);
        //response.then().assertThat().body(matchesJsonSchema(getJsonData("PlatformsSchema")));
    }

   // @Test(priority = 9)
    public void updateAssociationsBetweenFiltersAndPlatformTest() {
        platformIdList.forEach(System.out::println);
        System.out.println("================");
        String id = platformIdList.get(7);
        System.out.println(id);
        String endpoint = "/api/platforms/" + id + "/filters";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .body(getJsonData("FilterItemId"))
                .put(endpoint);
        for (int i = 0; i < platformIdList.size()-1; i+=2) {
            id = platformIdList.get(i);
            endpoint = "/api/platforms/" + id + "/filters";
            response = given()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + tokenAdmin.get())
                    .body(getJsonData("FilterItemId"))
                    .put(endpoint);
        }
        response.prettyPrint();
//        endpoint = "/api/platforms/257b6427-9c2e-4db3-aab6-aba406383605/filters";
//        response = given()
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + token.get())
//                .body(getJsonData("FilterItemId"))
//                .put(endpoint);
//        response.prettyPrint();
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("FilterItemsAssociationsSchema")));
    }

 //   @Test(priority = 10)
    public void updateAssociationsBetweenFiltersAndPlatformNegativeTest() {
        String id = platformIdList.get(0);
        String endpoint = "/api/platforms/" + id + 1 + "/filters";
        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .body(getJsonData("FilterItemId"))
                .put(endpoint);
        response.then().assertThat().statusCode(400);
        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");
        endpoint = "/api/platforms/" + id + "/filters";
        String filterItemId = "{\n" +
                "  \"idFilterItems\": [\n" +
                "    \"d39d6e49-cb3f-4359-a24d-2f6477fdce18\",\n" +
                "  ]\n" +
                "}";
        response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .body(filterItemId)
                .put(endpoint);
        response.then().assertThat().statusCode(400);
        Assert.assertEquals(response.getBody().jsonPath().getString("error"), "Bad Request");
    }

   // @Test(priority = 11)
    public void getListOfFilteredPlatforms() {
        String endpoint = "/api/platforms/filtration";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(getJsonData("FilterItemId"))
                .post(endpoint);
        response.prettyPrint();
        response.then().assertThat().statusCode(201);
        response.then().assertThat().body(matchesJsonSchema(getJsonData("FilteredPlatformsSchema")));
    }



    private void getPlatformIdList() throws JsonProcessingException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String endpoint = "/api/platforms";
        Response response = given()
                .header("Authorization", "Bearer " + tokenAdmin.get())
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

    public static void getUserId() {
        String endpoint = "/api/users/info";
        Response response = given()
                .header("Authorization", "Bearer " + tokenUser.get())
                .get(endpoint);
        userId = response.getBody().jsonPath().getString("id");
        log.debug("USER ID:  " + userId);
    }

    public static void getAdminId() {
        String endpoint = "/api/users/info";
        Response response = given()
                .header("Authorization", "Bearer " + tokenAdmin.get())
                .get(endpoint);
        adminId = response.getBody().jsonPath().getString("id");
        log.debug("USER ID:  " + adminId);
    }

    public String getJsonData(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("files/Platforms/" + fileName + ".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DataProvider(name = "Data for sorting")
    public Object[][] getDataSort() {
        return new Object[][]{
                {"sort1", "asc"},
                {"sort2", "desc"}
        };
    }
}
