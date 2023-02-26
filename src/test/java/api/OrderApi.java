package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.Order;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;

public class OrderApi {

    private final static String ORDER_CREATE_ENDPOINT = "/orders";
    private final static String USER_ORDERS_LIST_ENDPOINT = "/orders";
    private final RequestSpecification requestSpecification;
    private Order order;
    private List<Order> orders;

    public OrderApi(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
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
                     //   .body(order) // заполни body
                        .when()
                        .get(ORDER_CREATE_ENDPOINT); // отправь запрос на ручку
        return response;
    }

    @Step("Удалить заказ по number")
    public void deleteOrder(){
        Integer orderNumber =
                requestSpecification.given()
                        .body(order) // заполни body
                        .when()
                        .post(ORDER_CREATE_ENDPOINT) // отправь запрос на ручку
                        .then().extract().body().path("number");
        if (orderNumber != null) {
            requestSpecification.given()
                    .delete(ORDER_CREATE_ENDPOINT + "/{number}", orderNumber.toString()) // отправка DELETE-запроса
                    .then().assertThat().statusCode(SC_OK); // проверка, что сервер вернул код 200
        }
    }
}
