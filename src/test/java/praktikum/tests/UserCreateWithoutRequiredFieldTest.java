package praktikum.tests;

import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.client.UserClient;
import praktikum.model.User;

import java.util.UUID;

import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public class UserCreateWithoutRequiredFieldTest extends BaseTest {

    private final String email;
    private final String password;
    private final String name;

    public UserCreateWithoutRequiredFieldTest(
            String email,
            String password,
            String name
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        String uniqueEmail = "test-" + UUID.randomUUID() + "@yandex.ru";

        return new Object[][]{
                {null, "password123", "Test User"},
                {uniqueEmail, null, "Test User"},
                {uniqueEmail, "password123", null}
        };
    }

    @Test
    public void createUserWithoutRequiredFieldTest() {
        User user = new User(email, password, name);
        UserClient userClient = new UserClient();

        Response response = userClient.create(user);

        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }
}
