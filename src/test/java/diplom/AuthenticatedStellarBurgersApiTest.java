package diplom;

import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public abstract class AuthenticatedStellarBurgersApiTest extends StellarBurgersApiTest {

    protected String authenticatedName;
    protected String authenticatedEmail;
    protected String authenticatedPassword;
    protected String authenticatedAccessToken;

    @Before
    public void setUp() throws Exception {
        authenticatedName = UUID.randomUUID().toString();
        authenticatedEmail = authenticatedName + "@example.com";
        authenticatedPassword = UUID.randomUUID().toString();

        authenticatedAccessToken = given().contentType(ContentType.JSON)
                .body(Map.of(
                        "email", authenticatedEmail,
                        "password", authenticatedPassword,
                        "name", authenticatedName
                )).post("/auth/register")
                .body().jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() throws Exception {
        given().header("authorization", authenticatedAccessToken)
                .delete("/auth/user");
    }

}
