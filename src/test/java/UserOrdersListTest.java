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

public class UserOrdersListTest extends BaseApi {

    private OrderApi orderApi;
    private UserApi userApi;

    @Before
    public void setUp() {
        super.setupRequestSpecification();
        this.orderApi = new OrderApi(requestSpecification);
        this.userApi = new UserApi(requestSpecification);
        userApi.setUser(new User("g77750elenagromova@yandex.com", "1234", "Elena"));
        userApi.createUser().then().statusCode(SC_OK);
       for (int i = 0; i < 3; i++){
            orderApi.addOrder(new Order(new String[]{"60d3463f7034a000269f45e9",  "60d3463f7034a000269f45e7"}));
        }

    }

    //получить список заказов авторизованного юзера
    //запрос возвращает правильный код ответа и статус success;
    @Test
    @DisplayName("Проверка получения списка заказов (пользователь авторизован)")
    @Description("Успешная проверка создания заказа (пользователь авторизован)")
    public void checkGetOrdersListWithUserAuth(){
        orderApi.getUserOrdersList(userApi.getTokenUser())
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
        orderApi.getUserOrdersList(null)
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @After
    public void dataClean(){
        userApi.deleteUser();
//        for (Order order: orderApi.getOrders()) {
//            orderApi.deleteOrder();
//        }
    }

}
