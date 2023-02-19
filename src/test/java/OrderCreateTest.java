import api.BaseApi;
import api.OrderApi;
import api.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.Order;
import org.example.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class OrderCreateTest extends BaseApi {

    private OrderApi orderApi;
    private UserApi userApi;
    private String token;

    @Before
    public void setUp() {
        super.setupRequestSpecification();
        this.orderApi = new OrderApi(requestSpecification);
        this.userApi = new UserApi(requestSpecification);
        userApi.setUser(new User("g77733elenagromova@yandex.com", "1234", "Elena"));
        userApi.createUser().then().statusCode(SC_OK);
        token = userApi.getTokenUser();
    }

    //заказ можно создать авторизованным пользователем;
    //запрос возвращает правильный код ответа и статус success;
    @Test
    @DisplayName("Проверка создания заказа (пользователь авторизован)")
    @Description("Успешная проверка создания заказа (пользователь авторизован)")
    public void checkCreateOrderWithUserAuth(){
        orderApi.setOrder(new Order(new String[]{"60d3463f7034a000269f45e9",  "60d3463f7034a000269f45e7"}));
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
        orderApi.setOrder(new Order(new String[]{"60d3463f7034a000269f45e9",  "60d3463f7034a000269f45e7"}));
        orderApi.createOrder("")
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    //заказ нельзя создать без ингредиентов;
    //запрос возвращает правильный код ответа и статус success;
    @Test
    @DisplayName("Проверка создания заказа без ингредиентов")
    @Description("Неуспешная проверка создания заказа без ингредиентов")
    public void checkCreateOrderWithoutIngredients(){
        orderApi.setOrder(new Order(new String[]{}));
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
        orderApi.setOrder(new Order(new String[]{"ингредиент"}));
        orderApi.createOrder(userApi.getTokenUser())
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void dataClean(){
        userApi.deleteUser();
 //       orderApi.deleteOrder();
    }
}
