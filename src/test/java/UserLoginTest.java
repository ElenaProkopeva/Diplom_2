import api.BaseApi;
import api.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.*;

public class UserLoginTest extends BaseApi {

    private UserApi userApi;

    @Before
    public void setUp() {
        super.setupRequestSpecification();
        this.userApi = new UserApi(requestSpecification);
    }

    //юзер может авторизоваться;
    //успешный запрос возвращает accessToken
    @Test
    @DisplayName("Проверка авторизации юзера (успешно)")
    @Description("Успешная проверка логина юзера")
    public void checkLoginUser(){
        userApi.setUser(new User("g77716elenagromova@yandex.com", "1234", "Elena"));
        userApi.createUser();
        userApi.loginUser()
                .then().assertThat().body("success", is(true))
                .assertThat().body("accessToken", notNullValue())
                .assertThat().body("user.email", equalTo(userApi.getUser().getEmail()))
                .assertThat().body("user.name", equalTo(userApi.getUser().getName()))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Проверка авторизации юзера без email")
    @Description("Проверка, что нельзя залогиниться без ввода email")
    public void checkLoginUserWithoutEmail(){
        userApi.setUser(new User("", "1234", "Elena"));
        userApi.createUser();
        userApi.loginUser()
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Проверка авторизации юзера без пароля")
    @Description("Проверка, что нельзя залогиниться без ввода пароля")
    public void checkLoginUserWithoutPassword(){
        userApi.setUser(new User("g77716elenagromova@yandex.com", "", "Elena"));
        userApi.createUser();
        userApi.loginUser()
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Проверка авторизации юзера без данных")
    @Description("Проверка, что нельзя залогиниться без ввода данных")
    public void checkLoginUserWithoutAllData(){
        userApi.setUser(new User("", "", "Elena"));
        userApi.createUser();
        userApi.loginUser()
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @After
    public void dataClean(){
        userApi.deleteUser();
    }
}
