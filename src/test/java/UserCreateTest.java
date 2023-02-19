import api.BaseApi;
import api.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class UserCreateTest extends BaseApi {

    private UserApi userApi;

    @Before
    public void setUp() {
        super.setupRequestSpecification();
        this.userApi = new UserApi(requestSpecification);
    }

    //юзера можно создать;
    //запрос возвращает правильный код ответа и статус success;
    @Test
    @DisplayName("Проверка создания юзера (успешно)")
    @Description("Успешная проверка создания юзера")
    public void checkCreateUser(){
        userApi.setUser(new User("g77713elenagromova@yandex.com", "1234", "Elena"));
        userApi.createUser()
                .then().assertThat().body("success", is(true))
                .assertThat().body("user.email", equalTo(userApi.getUser().getEmail()))
                .assertThat().body("user.name", equalTo(userApi.getUser().getName()))
                .and()
                .statusCode(SC_OK);
    }

    //дубль юзера нельзя создать;
    //запрос возвращает правильный код ответа, message и success;
    @Test
    @DisplayName("Проверка создания дубля юзера")
    @Description("Проверка, что нельзя создать дубль юзера")
    public void checkCreateDuplicateUser(){
        userApi.setUser(new User("g77713elenagromova@yandex.com", "1234", "Elena"));
        userApi.createUser();
        userApi.createUser()
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("User already exists"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    //чтобы создать юзера, нужно передать в ручку все обязательные поля
    //если одного из полей нет, запрос возвращает ошибку;
    @Test
    @DisplayName("Проверка создания юзера без Email")
    @Description("Проверка, что нельзя создать юзера без Email")
    public void checkCreateUserWithoutEmail(){
        userApi.setUser(new User("", "1234", "Elena"));
        userApi.createUser()
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    //чтобы создать юзера, нужно передать в ручку все обязательные поля
    //если одного из полей нет, запрос возвращает ошибку;
    @Test
    @DisplayName("Проверка создания юзера без пароля")
    @Description("Проверка, что нельзя создать юзера без пароля")
    public void checkCreateUserWithoutPassword(){
        userApi.setUser(new User("g77713elenagromova@yandex.com", "", "Elena"));
        userApi.createUser()
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    //чтобы создать юзера, нужно передать в ручку все обязательные поля
    //если одного из полей нет, запрос возвращает ошибку;
    @Test
    @DisplayName("Проверка создания юзера без имени")
    @Description("Проверка, что нельзя создать юзера без имени")
    public void checkCreateUserWithoutName(){
        userApi.setUser(new User("g77713elenagromova@yandex.com", "1234", ""));
        userApi.createUser()
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    //чтобы создать юзера, нужно передать в ручку все обязательные поля
    //если одного из полей нет, запрос возвращает ошибку;
    @Test
    @DisplayName("Проверка создания юзера без данных")
    @Description("Проверка, что нельзя создать юзера без данных")
    public void checkCreateUserWithoutAllData(){
        userApi.setUser(new User("", "", ""));
        userApi.createUser()
                .then().assertThat().body("success", is(false))
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @After
    public void dataClean(){
        userApi.deleteUser();
    }

}

