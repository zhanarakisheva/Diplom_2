package diplom;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LoginTest {

    @Before
    public void setUp() throws Exception {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
    }

    @Test
    public void loginExistingUserTest() {
        String name = UUID.randomUUID().toString();
        String email = name + "@example.com";

        given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test",
                        "name", name
                )).post("/auth/register");

        given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test"
                ))
                .when().post("/auth/login")
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
    public void loginWrongCredentialsTest() {
        String name = UUID.randomUUID().toString();
        String email = name + "@example.com";

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test"
                ))
                .when().post("/auth/login")
                .then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

}
