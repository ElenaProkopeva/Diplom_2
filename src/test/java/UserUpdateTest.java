import api.BaseApi;
import api.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserUpdateTest extends BaseApi {

    private final static String USER_LOGIN = "g77719elenagromova@yandex.com";
    private final static String USER_PASSWORD = "1234";
    private final static String USER_NAME = "Elena";
    private final static String USER_NEW_NAME = "Elena1";

    private UserApi userApi;
    private String token;

    @Before
    public void setUp() {
        super.setupRequestSpecification();
        this.userApi = new UserApi(requestSpecification);
        userApi.setUser(new User(USER_LOGIN, USER_PASSWORD, USER_NAME));
        userApi.createUser().then().statusCode(SC_OK);
        token = userApi.getTokenUser();
    }

    //изменение юзера;
    //успешный запрос
    @Test
    @DisplayName("Проверка изменения юзера (успешно)")
    @Description("Успешное изменение юзера")
    public void checkUpdateUser(){
        User user = new User(USER_LOGIN, USER_PASSWORD, USER_NEW_NAME);
        userApi.updateUser(user, token)
                .then().assertThat().body("success", is(true))
                .assertThat().body("user.email", equalTo(user.getEmail()))
                .assertThat().body("user.name", equalTo(user.getName()))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Проверка изменения юзера без токена")
    @Description("Проверка, что нельзя изменить юзера без авторизации по токену")
    public void checkUpdateUserWithoutToken(){
        User user = new User(USER_LOGIN, USER_PASSWORD, USER_NEW_NAME);
        userApi.updateUser(user, "")
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Проверка изменения юзера на дубликат")
    @Description("Проверка, что можно изменить юзера на дубликат")
    public void checkUpdateUserWithDublicateEmail(){
        User user = new User(USER_LOGIN, USER_PASSWORD, USER_NAME);
        userApi.updateUser(user, token)
                .then().assertThat().body("success", is(true))
                .assertThat().body("user.email", equalTo(user.getEmail()))
                .assertThat().body("user.name", equalTo(user.getName()))
                .and()
                .statusCode(SC_OK);
    }

    @After
    public void dataClean(){
        userApi.deleteUser(userApi.getTokenUser());
    }
}
