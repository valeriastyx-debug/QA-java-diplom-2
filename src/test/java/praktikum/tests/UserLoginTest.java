package praktikum.tests;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.model.User;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class UserLoginTest extends BaseTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Test
    public void loginExistingUserTest() {
        String email = "test-" + UUID.randomUUID() + "@yandex.ru";
        String password = "password123";
        String name = "Test User";

        User user = new User(email, password, name);

        // Сначала регистрируем пользователя
        Response createResponse = userClient.create(user);

        createResponse.then()
                .statusCode(200)
                .body("success", is(true));

        // Сохраняем токен, чтобы удалить пользователя после теста
        accessToken = createResponse.path("accessToken");

        // Выполняем вход с данными зарегистрированного пользователя
        Response loginResponse = userClient.login(user);

        // Проверяем успешную авторизацию
        loginResponse.then()
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Test
    public void loginWithIncorrectCredentialsTest() {
        String email = "wrong-" + UUID.randomUUID() + "@yandex.ru";
        String password = "wrongPassword";
        String name = "Test User";

        User user = new User(email, password, name);

        Response loginResponse = userClient.login(user);

        loginResponse.then()
                .statusCode(401)
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
