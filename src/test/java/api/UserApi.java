package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.User;

import static org.apache.http.HttpStatus.SC_ACCEPTED;

public class UserApi {

    private final static String USER_CREATE_ENDPOINT = "/auth/register";
    private final static String USER_LOGIN_ENDPOINT = "/auth/login";
    private final static String USER_UPDATE_ENDPOINT = "/auth/user";
    private final RequestSpecification requestSpecification;
    private User user;

    public UserApi(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Step("Создать юзера и проверить статус ответа")
    public Response createUser(){
        Response response =
                requestSpecification
                        .given()
                        .body(user) // заполни body
                        .when()
                        .post(USER_CREATE_ENDPOINT); // отправь запрос на ручку
        return response;
    }

    @Step("Авторизоваться под юзером")
    public Response loginUser(){
        Response response =
                requestSpecification
                        .given()
                        .body(user) // заполни body
                        .when()
                        .post(USER_LOGIN_ENDPOINT); // отправь запрос на ручку
        return response;
    }

    @Step("Авторизоваться под юзером и получить его токен")
    public String getTokenUser(){
        String token = "";
        String userToken =
                requestSpecification.given()
                        .body(user) // заполни body
                        .when()
                        .post(USER_LOGIN_ENDPOINT) // отправь запрос на ручку
                        .then().extract().body().path("accessToken");
        if (userToken != null) {
            token = userToken.replace("Bearer ", "");
        }
        return token;
    }

    @Step("Изменение данных юзера")
    public Response updateUser(User user, String token){
        Response response =
                requestSpecification
                        .given()
                        .auth().oauth2(token)
                        .body(user)
                        .when()
                        .patch(USER_UPDATE_ENDPOINT); // отправь запрос на ручку
        return response;
    }

    @Step("Удалить юзера по id")
    public void deleteUser(){
        String userToken =
                requestSpecification.given()
                        .body(user) // заполни body
                        .when()
                        .post(USER_LOGIN_ENDPOINT) // отправь запрос на ручку
                        .then().extract().body().path("accessToken");
        if (userToken != null) {
            String token = userToken.replace("Bearer ", "");
            requestSpecification.given()
                    .auth().oauth2(token)
                    .delete(USER_UPDATE_ENDPOINT) // отправка DELETE-запроса
                    .then().assertThat().statusCode(SC_ACCEPTED); // проверка, что сервер вернул код 200
        }
    }
}
