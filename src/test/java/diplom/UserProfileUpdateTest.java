package diplom;

import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserProfileUpdateTest extends StellarBurgersApiTest {

    @Test
    public void updateNameTest() {
        String name = UUID.randomUUID().toString();
        String email = name + "@example.com";

        String accessToken = given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test",
                        "name", name
                )).post("/auth/register")
                .body().jsonPath().getString("accessToken");

        String newName = UUID.randomUUID().toString();
        given().contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .body(Map.of(
                        "name", newName
                ))
                .when().patch("/auth/user")
                .then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(newName));

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
    public void updateEmailTest() {
        String name = UUID.randomUUID().toString();
        String email = name + "@example.com";

        String accessToken = given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", "test",
                        "name", name
                )).post("/auth/register")
                .body().jsonPath().getString("accessToken");

        String newEmail = UUID.randomUUID()  + "@example.com";
        given().contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .body(Map.of(
                        "email", newEmail
                ))
                .when().patch("/auth/user")
                .then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(name));

        given().header("authorization",
                        given().contentType(ContentType.JSON)
                                .body(Map.of(
                                        "email", newEmail,
                                        "password", "test"
                                ))
                                .when().post("/auth/login")
                                .body().jsonPath().getString("accessToken"))
                .delete("/auth/user");
    }

    @Test
    public void updateProfileUnauthorizedTest() {
        String newName = UUID.randomUUID().toString();
        given().contentType(ContentType.JSON)
                .body(Map.of(
                        "name", newName
                ))
                .when().patch("/auth/user")
                .then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

}
