package praktikum.client;

import io.restassured.response.Response;
import praktikum.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDERS_PATH = "/api/orders";
    private static final String INGREDIENTS_PATH = "/api/ingredients";

    public Response getIngredients() {
        return given()
                .get(INGREDIENTS_PATH);
    }

    public Response create(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(ORDERS_PATH);
    }

    public Response createWithAuthorization(Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .post(ORDERS_PATH);
    }
}
