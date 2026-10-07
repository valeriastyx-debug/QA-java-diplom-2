package praktikum.tests;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import praktikum.client.OrderClient;
import praktikum.client.UserClient;
import praktikum.model.Order;
import praktikum.model.User;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreateTest extends BaseTest {

    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();

    private String accessToken;

    @Test
    public void createOrderWithAuthorizationAndIngredientsTest() {

        // Создаём уникального пользователя
        String email = "test-" + UUID.randomUUID() + "@yandex.ru";
        String password = "password123";
        String name = "Test User";

        User user = new User(email, password, name);

        Response userResponse = userClient.create(user);

        userResponse.then()
                .statusCode(200)
                .body("success", is(true));

        accessToken = userResponse.path("accessToken");

        // Получаем настоящий ингредиент с сервера
        Response ingredientsResponse = orderClient.getIngredients();

        ingredientsResponse.then()
                .statusCode(200)
                .body("success", is(true));

        String ingredientId = ingredientsResponse.path("data[0]._id");

        // Создаём заказ
        Order order = new Order(Arrays.asList(ingredientId));

        Response orderResponse =
                orderClient.createWithAuthorization(order, accessToken);

        // Проверяем ответ
        orderResponse.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order", notNullValue());
    }

    @Test
    public void createOrderWithoutAuthorizationTest() {

        // Получаем настоящий ингредиент с сервера
        Response ingredientsResponse = orderClient.getIngredients();

        ingredientsResponse.then()
                .statusCode(200)
                .body("success", is(true));

        String ingredientId = ingredientsResponse.path("data[0]._id");

        Order order = new Order(Arrays.asList(ingredientId));

        // Создаём заказ без токена авторизации
        Response orderResponse = orderClient.create(order);

        orderResponse.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order", notNullValue());
    }

    @Test
    public void createOrderWithoutIngredientsTest() {

        Order order = new Order(Arrays.asList());

        Response orderResponse = orderClient.create(order);

        orderResponse.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWithInvalidIngredientHashTest() {

        String invalidIngredientHash = "invalidIngredientHash123";

        Order order = new Order(Arrays.asList(invalidIngredientHash));

        Response orderResponse = orderClient.create(order);

        orderResponse.then()
                .statusCode(500);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}
