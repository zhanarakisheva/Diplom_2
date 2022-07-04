package diplom;

import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetOrdersTest extends AuthenticatedStellarBurgersApiTest {

    @Test
    public void authorizedTest() {
        List<String> ingredients = getAllIngredientIds();

        int orderNumber = given().contentType(ContentType.JSON)
                .header("authorization", authenticatedAccessToken)
                .body(Map.of(
                        "ingredients", ingredients
                ))
                .post("/orders")
                .body().jsonPath().getInt("order.number");

        given().contentType(ContentType.JSON)
                .header("authorization", authenticatedAccessToken)
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
