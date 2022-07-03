package diplom;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CreateOrdersTest {

    @Before
    public void setUp() throws Exception {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
    }

    @Test
    public void authorizedWithIngredientsTest() {
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

        given().contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .body(Map.of(
                        "ingredients", ingredients
                ))
                .when().post("/orders")
                .then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());

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
    public void authorizedWithoutIngredientsTest() {
        String name = UUID.randomUUID().toString();
        String email = name + "@example.com";

        String accessToken = given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test",
                        "name", name
                )).post("/auth/register")
                .body().jsonPath().getString("accessToken");

        given().contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .body(Map.of(
                        "ingredients", List.of()
                ))
                .when().post("/orders")
                .then().assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));

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
    public void authorizedInvalidIngredientsTest() {
        String name = UUID.randomUUID().toString();
        String email = name + "@example.com";

        String accessToken = given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test",
                        "name", name
                )).post("/auth/register")
                .body().jsonPath().getString("accessToken");

        given().contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .body(Map.of(
                        "ingredients", List.of(UUID.randomUUID().toString())
                ))
                .when().post("/orders")
                .then().assertThat()
                .statusCode(500);

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
    public void unauthorizedWithIngredientsTest() {
        List<String> ingredients = get("/ingredients").body()
                .jsonPath().getList("data._id", String.class);

        given().contentType(ContentType.JSON)
                .body(Map.of(
                        "ingredients", ingredients
                ))
                .when().post("/orders")
                .then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    public void unauthorizedWithoutIngredientsTest() {
        given().contentType(ContentType.JSON)
                .body(Map.of(
                        "ingredients", List.of()
                ))
                .when().post("/orders")
                .then().assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void unauthorizedInvalidIngredientsTest() {
        given().contentType(ContentType.JSON)
                .body(Map.of(
                        "ingredients", List.of(UUID.randomUUID().toString())
                ))
                .when().post("/orders")
                .then().assertThat()
                .statusCode(500);
    }

}
