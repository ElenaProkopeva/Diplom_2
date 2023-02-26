package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.Order;

public class OrderApi {

    private final static String ORDER_CREATE_ENDPOINT = "/orders";
    private final RequestSpecification requestSpecification;
    private Order order;

    public OrderApi(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Step("Создать заказ")
    public Response createOrder(String token){
        Response response =
                requestSpecification
                        .given()
                        .auth().oauth2(token)
                        .body(order) // заполни body
                        .when()
                        .post(ORDER_CREATE_ENDPOINT); // отправь запрос на ручку
        return response;
    }

    @Step("Получить список заказов пользователя")
    public Response getUserOrdersList(String token){
        Response response =
                requestSpecification
                        .given()
                        .auth().oauth2(token)
                        .when()
                        .get(ORDER_CREATE_ENDPOINT); // отправь запрос на ручку
        return response;
    }

}
