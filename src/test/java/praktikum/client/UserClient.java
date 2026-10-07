package praktikum.client;

import io.restassured.response.Response;
import praktikum.model.User;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String CREATE_USER_PATH = "/api/auth/register";
    private static final String LOGIN_USER_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";

    public Response create(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(CREATE_USER_PATH);
    }

    public Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(LOGIN_USER_PATH);
    }

    public Response delete(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .delete(USER_PATH);
    }
}
