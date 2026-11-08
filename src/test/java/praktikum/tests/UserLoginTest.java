
package praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.model.User;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class UserLoginTest extends BaseTest {

    private final UserClient userClient = new UserClient();

    private User user;
    private String accessToken;

    @Before
    public void setUpUser() {
        String email = "test-" + UUID.randomUUID() + "@yandex.ru";
        String password = "password123";
        String name = "Test User";

        user = new User(email, password, name);

        Response createResponse = userClient.create(user);

        createResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));

        accessToken = createResponse.path("accessToken");
    }

    @Test
    @DisplayName("Авторизация существующего пользователя")
    @Description("Проверка успешной авторизации зарегистрированного пользователя")
    public void loginExistingUserTest() {

        Response loginResponse = userClient.login(user);

        loginResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    @Description("Проверка ошибки авторизации при неверном email и правильном пароле")
    public void loginWithIncorrectEmailTest() {

        User incorrectUser = new User(
                "wrong-" + UUID.randomUUID() + "@yandex.ru",
                user.getPassword(),
                user.getName()
        );

        Response loginResponse = userClient.login(incorrectUser);

        loginResponse.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    @Description("Проверка ошибки авторизации при правильном email и неверном пароле")
    public void loginWithIncorrectPasswordTest() {

        User incorrectUser = new User(
                user.getEmail(),
                "wrongPassword",
                user.getName()
        );

        Response loginResponse = userClient.login(incorrectUser);

        loginResponse.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}
