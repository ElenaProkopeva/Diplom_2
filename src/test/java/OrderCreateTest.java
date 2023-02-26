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

public class OrderCreateTest extends BaseApi {

    private final static String USER_LOGIN = "g77733elenagromova@yandex.com";
    private final static String USER_PASSWORD = "1234";
    private final static String USER_NAME = "Elena";

    private OrderApi orderApi;
    private Order order;
    private UserApi userApi;
    private IngredientApi ingredientApi;
    private String token;
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
    }

    //заказ можно создать авторизованным пользователем;
    //запрос возвращает правильный код ответа и статус success;
    @Test
    @DisplayName("Проверка создания заказа (пользователь авторизован)")
    @Description("Успешная проверка создания заказа (пользователь авторизован)")
    public void checkCreateOrderWithUserAuth(){
        orderApi.setOrder(order);
        orderApi.createOrder(token)
                .then()
                .assertThat().body("success", is(true))
                .assertThat().body("name", notNullValue())
                .assertThat().body("order.number", notNullValue())
                .and()
                .statusCode(SC_OK);
    }

    //заказ нельзя создать неавторизованным пользователем;
    //запрос возвращает правильный код ответа и статус success;
    @Test
    @DisplayName("Проверка создания заказа (пользователь не авторизован)")
    @Description("Неуспешная проверка создания заказа (пользователь не авторизован)")
    public void checkCreateOrderWithoutUserAuth(){
        orderApi.setOrder(order);
        orderApi.createOrder("123")
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("jwt malformed"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    //заказ нельзя создать без ингредиентов;
    //запрос возвращает правильный код ответа и статус success;
    @Test
    @DisplayName("Проверка создания заказа без ингредиентов")
    @Description("Неуспешная проверка создания заказа без ингредиентов")
    public void checkCreateOrderWithoutIngredients(){
        orderApi.setOrder(new Order(null));
        orderApi.createOrder(token)
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    //заказ нельзя создать с некорректным кодом ингредиентов;
    //запрос возвращает правильный код ответа и статус success;
    @Test
    @DisplayName("Проверка создания заказа с некорректным кодом ингредиентов")
    @Description("Неуспешная проверка создания заказа с некорректным кодом ингредиентов")
    public void checkCreateOrderWithIncorrectIngredientCode(){
        orderApi.setOrder(new Order(List.of("ингредиент")));
        orderApi.createOrder(userApi.getTokenUser())
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void dataClean(){
        userApi.deleteUser(userApi.getTokenUser());
    }
}
