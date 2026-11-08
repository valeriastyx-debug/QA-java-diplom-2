
package praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import praktikum.client.OrderClient;
import praktikum.client.UserClient;
import praktikum.model.Order;
import praktikum.model.User;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreateTest extends BaseTest {

    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();

    private String accessToken;

    @Test
    @DisplayName("Создание заказа авторизованным пользователем")
    @Description("Проверка успешного создания заказа с валидным ингредиентом и токеном авторизации")
    public void createOrderWithAuthorizationAndIngredientsTest() {

        // Создаём уникального пользователя
        String email = "test-" + UUID.randomUUID() + "@yandex.ru";
        String password = "password123";
        String name = "Test User";

        User user = new User(email, password, name);

        Response userResponse = userClient.create(user);

        userResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));

        accessToken = userResponse.path("accessToken");

        // Получаем ингредиент с сервера
        Response ingredientsResponse = orderClient.getIngredients();

        ingredientsResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));

        String ingredientId = ingredientsResponse.path("data[0]._id");

        // Создаём заказ с авторизацией
        Order order = new Order(
                Collections.singletonList(ingredientId)
        );

        Response orderResponse =
                orderClient.createWithAuthorization(order, accessToken);

        // Проверяем ответ
        orderResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка успешного создания заказа без токена авторизации с валидным ингредиентом")
    public void createOrderWithoutAuthorizationTest() {

        // Получаем ингредиент с сервера
        Response ingredientsResponse = orderClient.getIngredients();

        ingredientsResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));

        String ingredientId = ingredientsResponse.path("data[0]._id");

        Order order = new Order(
                Collections.singletonList(ingredientId)
        );

        // Создаём заказ без авторизации
        Response orderResponse = orderClient.create(order);

        orderResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка ошибки при создании заказа с пустым списком ингредиентов")
    public void createOrderWithoutIngredientsTest() {

        Order order = new Order(Collections.emptyList());

        Response orderResponse = orderClient.create(order);

        orderResponse.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с некорректным хешем ингредиента")
    @Description("Проверка ответа сервера при создании заказа с несуществующим хешем ингредиента")
    public void createOrderWithInvalidIngredientHashTest() {

        String invalidIngredientHash = "invalidIngredientHash123";

        Order order = new Order(
                Collections.singletonList(invalidIngredientHash)
        );

        Response orderResponse = orderClient.create(order);

        orderResponse.then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}
