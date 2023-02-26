package api;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;

public class BaseApi {

    protected RequestSpecification requestSpecification;

    @Before
    public void setupRequestSpecification()
    {
        requestSpecification = RestAssured.given()
                .baseUri("https://stellarburgers.nomoreparties.site")
                .basePath("/api")
                .header("Content-type", "application/json");
    }
}
