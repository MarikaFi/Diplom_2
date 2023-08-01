package client;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import form.LoginForm;
import form.CreateUserForm;
import form.PatchForm;

public class Client extends StellarBurgersRestClient {

    private static final String AUTH_PATH = "/api/auth";

    @Step("Send POST Request /api/auth/register")
    public ValidatableResponse createUser(CreateUserForm body) {
        return given()
                .spec(getBaseSpec())
                .body(body)
                .when()
                .post(AUTH_PATH + "/register")
                .then();
    }

    @Step("Send POST Request /api/auth/login")
    public ValidatableResponse loginUser(LoginForm body) {
        return given()
                .spec(getBaseSpec())
                .body(body)
                .when()
                .post(AUTH_PATH + "/login")
                .then();
    }

    @Step("Send GET Request /api/auth/user")
    public ValidatableResponse getUserInfo(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .get(AUTH_PATH + "/user")
                .then();
    }

    @Step("Send PATCH Request /api/auth/user")
    public ValidatableResponse patchUserInfo(String token, PatchForm body) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .body(body)
                .when()
                .patch(AUTH_PATH + "/user")
                .then();
    }

    @Step("Send DELETE Request /api/auth/user")
    public ValidatableResponse deleteUser(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .delete(AUTH_PATH + "/user")
                .then();
    }
}
