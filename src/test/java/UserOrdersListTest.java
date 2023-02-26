import api.BaseApi;
import api.IngredientApi;
import api.OrderApi;
import api.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.Order;
import org.example.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserOrdersListTest extends BaseApi {

    private final static String USER_LOGIN = "g77750elenagromova@yandex.com";
    private final static String USER_PASSWORD = "1234";
    private final static String USER_NAME = "Elena";

    private OrderApi orderApi;
    private UserApi userApi;
    private String token;
    private Order order;
    private IngredientApi ingredientApi;
    private List<String> ingredients;

    @Before
    public void setUp() {
        super.setupRequestSpecification();
        this.orderApi = new OrderApi(requestSpecification);
        this.userApi = new UserApi(requestSpecification);
        this.ingredientApi = new IngredientApi(requestSpecification);
        userApi.setUser(new User(USER_LOGIN, USER_PASSWORD, USER_NAME));
        userApi.createUser().then().statusCode(SC_OK);
        token = userApi.getTokenUser();
        ingredients = ingredientApi.getIngredientsList();
        order = new Order(List.of(ingredients.get(0), ingredients.get(1)));
        for (int i = 0; i < 3; i++){
           orderApi.setOrder(order);
           orderApi.createOrder(token);
        }

    }

    //получить список заказов авторизованного юзера
    //запрос возвращает правильный код ответа и статус success;
    @Test
    @DisplayName("Проверка получения списка заказов (пользователь авторизован)")
    @Description("Успешная проверка создания заказа (пользователь авторизован)")
    public void checkGetOrdersListWithUserAuth(){
        orderApi.getUserOrdersList(token)
                .then().assertThat().body("success", is(true))
                .assertThat().body("total", notNullValue())
                .assertThat().body("orders", notNullValue())
                .and()
                .statusCode(SC_OK);
    }

    //получить список заказов неавторизованного юзера
    //запрос возвращает правильный код ответа и статус success;
    @Test
    @DisplayName("Проверка получения списка заказов (пользователь не авторизован)")
    @Description("Успешная проверка создания заказа (пользователь не авторизован)")
    public void checkGetOrdersListWithoutUserAuth(){
        orderApi.getUserOrdersList("null")
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("jwt malformed"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @After
    public void dataClean(){
        userApi.deleteUser(userApi.getTokenUser());
    }

}
