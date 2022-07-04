package diplom;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

import java.util.List;

import static io.restassured.RestAssured.get;

public abstract class StellarBurgersApiTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
    }

    protected List<String> getAllIngredientIds() {
        return get("/ingredients").body()
                .jsonPath().getList("data._id", String.class);
    }

}
