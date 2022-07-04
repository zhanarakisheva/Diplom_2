package diplom;

import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrdersTest extends AuthenticatedStellarBurgersApiTest {

    @Test
    public void authorizedWithIngredientsTest() {
        List<String> ingredients = getAllIngredientIds();

        given().contentType(ContentType.JSON)
                .header("authorization", authenticatedAccessToken)
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
    public void authorizedWithoutIngredientsTest() {
        given().contentType(ContentType.JSON)
                .header("authorization", authenticatedAccessToken)
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
    public void authorizedInvalidIngredientsTest() {
        given().contentType(ContentType.JSON)
                .header("authorization", authenticatedAccessToken)
                .body(Map.of(
                        "ingredients", List.of(UUID.randomUUID().toString())
                ))
                .when().post("/orders")
                .then().assertThat()
                .statusCode(500);
    }

    @Test
    public void unauthorizedWithIngredientsTest() {
        List<String> ingredients = getAllIngredientIds();

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
