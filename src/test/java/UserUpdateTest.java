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

    private UserApi userApi;

    @Before
    public void setUp() {
        super.setupRequestSpecification();
        this.userApi = new UserApi(requestSpecification);
    }

    //изменение юзера;
    //успешный запрос
    @Test
    @DisplayName("Проверка изменения юзера (успешно)")
    @Description("Успешное изменение юзера")
    public void checkUpdateUser(){
        userApi.setUser(new User("g77719elenagromova@yandex.com", "1234", "Elena"));
        userApi.createUser();
        User user = new User("g77719elenagromova1@yandex.com", "1234", "Elena1");
        userApi.updateUser(user, userApi.getTokenUser())
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
        userApi.setUser(new User("g77719elenagromova@yandex.com", "1234", "Elena"));
        userApi.createUser();
        User user = new User("g77719elenagromova1@yandex.com", "1234", "Elena1");
        userApi.updateUser(user, "")
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Проверка изменения юзера на дубликат")
    @Description("Проверка, что нельзя изменить юзера на дубликат")
    public void checkUpdateUserWithDublicateEmail(){
        userApi.setUser(new User("g77719elenagromova@yandex.com", "1234", "Elena"));
        userApi.createUser();
        User user = new User("g77719elenagromova1@yandex.com", "1234", "Elena");
        userApi.updateUser(user, userApi.getTokenUser())
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("User with such email already exists"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @After
    public void dataClean(){
        userApi.deleteUser();
    }
}
