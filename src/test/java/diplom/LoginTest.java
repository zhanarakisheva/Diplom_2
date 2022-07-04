package diplom;

import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LoginTest extends AuthenticatedStellarBurgersApiTest {

    @Test
    public void loginExistingUserTest() {
        given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", authenticatedEmail,
                        "password", authenticatedPassword
                ))
                .when().post("/auth/login")
                .then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(authenticatedEmail))
                .body("user.name", equalTo(authenticatedName))
                .body("accessToken", startsWith("Bearer "))
                .body("accessToken", not(equalTo("Bearer ")))
                .body("refreshToken", notNullValue());
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
