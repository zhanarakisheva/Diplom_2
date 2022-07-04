package diplom;

import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RegisterTest extends StellarBurgersApiTest {

    @Test
    public void registerUniqueUserTest() {
        String name = UUID.randomUUID().toString();
        String email = name + "@example.com";

        given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test",
                        "name", name
                ))
                .when().post("/auth/register")
                .then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", startsWith("Bearer "))
                .body("accessToken", not(equalTo("Bearer ")))
                .body("refreshToken", notNullValue());

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
    public void registerExistingUserTest() {
        String name = UUID.randomUUID().toString();
        String email = name + "@example.com";

        String accessToken = given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test",
                        "name", name
                )).post("/auth/register")
                .body().jsonPath().getString("accessToken");

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test",
                        "name", name
                ))
                .when().post("/auth/register")
                .then().assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));

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
    public void registerMissingFieldTest() {
        String name = UUID.randomUUID().toString();
        String email = name + "@example.com";

        given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test"
                ))
                .when().post("/auth/register")
                .then().assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

}
