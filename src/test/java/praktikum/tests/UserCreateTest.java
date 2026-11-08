package praktikum.tests;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.model.User;

import java.util.UUID;

import static org.hamcrest.Matchers.is;

public class UserCreateTest extends BaseTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Test
    public void createUniqueUserTest() {
        String email = "test-" + UUID.randomUUID() + "@yandex.ru";
        String password = "password123";
        String name = "Test User";

        User user = new User(email, password, name);

        Response response = userClient.create(user);

        response.then()
                .statusCode(200)
                .body("success", is(true));

        accessToken = response.path("accessToken");
    }

    @Test
    public void createUserThatAlreadyExistsTest() {
        String email = "test-" + UUID.randomUUID() + "@yandex.ru";
        String password = "password123";
        String name = "Test User";

        User user = new User(email, password, name);

        Response firstResponse = userClient.create(user);

        firstResponse.then()
                .statusCode(200)
                .body("success", is(true));

        accessToken = firstResponse.path("accessToken");

        Response secondResponse = userClient.create(user);

        secondResponse.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}
