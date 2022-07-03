package diplom;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBody;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetOrdersTest {

    @Before
    public void setUp() throws Exception {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
    }

    @Test
    public void authorizedTest() {
        String name = UUID.randomUUID().toString();
        String email = name + "@example.com";

        String accessToken = given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test",
                        "name", name
                )).post("/auth/register")
                .body().jsonPath().getString("accessToken");

        List<String> ingredients = get("/ingredients").body()
                .jsonPath().getList("data._id", String.class);

        int orderNumber = given().contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .body(Map.of(
                        "ingredients", ingredients
                ))
                .post("/orders")
                .body().jsonPath().getInt("order.number");

        given().contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .when().get("/orders")
                .then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", iterableWithSize(1))
                .body("orders[0].ingredients", equalTo(ingredients))
                .body("orders[0]._id", notNullValue())
                .body("orders[0].status", notNullValue())
                .body("orders[0].number", equalTo(orderNumber))
                .body("orders[0].createdAt", notNullValue())
                .body("orders[0].updatedAt", notNullValue())
                .body("total", notNullValue())
                .body("totalToday", notNullValue());

        given().header("authorization",
                        given().contentType(ContentType.JSON)
                                .body(Map.of(
                                        "email", email,
                                        "password", "test"
                                ))
                                .when().post("/auth/login")
                                .body().jsonPath().getString("accessToken"))
                .delete("/auth/user");
    }

    @Test
    public void unauthorizedTest() {
        given().contentType(ContentType.JSON)
                .when().get("/orders")
                .then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

}
