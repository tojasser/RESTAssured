package API;

import static io.restassured.RestAssured.*;

import groovyjarjarantlr4.v4.codegen.model.SrcOp;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class APITests {

    String endPoint = "https://api.intigral-ott.net/popcorn-api-rs-7.9.17/v1/promotions?apikey=webB2BGDMSTGExy0sVDlZMzNDdUyZ";


    @Test
    void testRessponseCodeIs200() {
        Response ressponse = get(endPoint);
        int responseCode = ressponse.getStatusCode();
        Assert.assertEquals(responseCode, 200);
    }

    @Test
    void propertiesPresentInJSON() {
        get(endPoint).then().body("$", hasKey("promotions"));

        Response res = given().when().get(endPoint);

        JsonPath j = new JsonPath(res.asString());
        int size = j.getInt("promotions.size()");
        // System.out.println(size);
        for (int i = 0; i < size; i++) {
            if (!j.getString("promotions[" + i + "]").isEmpty()) {
                Assert.assertTrue(!j.getString("promotions[" + i + "].promotionId").isEmpty());
                Assert.assertTrue(!j.getString("promotions[" + i + "].orderId").isEmpty());
                Assert.assertTrue(!j.getString("promotions[" + i + "].promoArea").isEmpty());
                Assert.assertTrue(!j.getString("promotions[" + i + "].promoType").isEmpty());

                Assert.assertTrue(isBoolean(j.getString("promotions[" + i + "].showPrice")));
                Assert.assertTrue(isBoolean(j.getString("promotions[" + i + "].showText")));
                Assert.assertNotNull(j.getString("promotions[" + i + "].localizedTexts.ar"));
                Assert.assertNotNull(j.getString("promotions[" + i + "].localizedTexts.en"));

            }

        }


    }

    boolean isBoolean(String value) {
        return value != null && Arrays.stream(new String[]{"true", "false"})
                .anyMatch(b -> b.equalsIgnoreCase(value));
    }


    @Test
    void promotionIdIsString() {

        Response res = given().when().get(endPoint);
        JsonPath j = new JsonPath(res.asString());
        int size = j.getInt("promotions.size()");
        for (int i = 0; i < size; i++) {

            get(endPoint).then().body("promotions[" + i + "].promotionId", isA(String.class));
        }
    }

    @Test
    void programTypeValidation() {
        Response res = given().when().get(endPoint);

        JsonPath j = new JsonPath(res.asString());

        int size = j.getInt("promotions.size()");
        for (int i = 0; i < size; i++) {
            if (j.getString("promotions[" + i + "].properties[0].programType") != null) {
                Assert.assertTrue(programTypeContentValidation(j.getString("promotions[" + i + "].properties[0].programType")));
                // System.out.println(j.getString("promotions["+i+"].properties[0].programType"));
            }


        }


    }

    boolean programTypeContentValidation(String value) {
        return value != null && Arrays.stream(new String[]{"EPISODE", "MOVIE", "SERIES", "SEASON"})
                .anyMatch(b -> b.equalsIgnoreCase(value));
    }


    @Test
    void inValidAPIKey() {
        get(endPoint + "invalid").then()
                .statusCode(HttpStatus.SC_FORBIDDEN).assertThat()
                .body("error.requestId", notNullValue())
                .body("error.code", equalTo("8001"))
                .body("error.message", equalTo("invalid api key"));
    }


}

