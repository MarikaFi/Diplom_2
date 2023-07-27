package client;

import static io.restassured.RestAssured.given;

import form.CreateOrderForm;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

public class OrdersClient extends StellarBurgersRestClient {

    private static final String ORDERS_PATH = "/api/orders";

    @Step("Send POST Request /api/orders")
    public ValidatableResponse createOrder(CreateOrderForm body) {
        return given()
                .spec(getBaseSpec())
                .body(body)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Send POST Request /api/orders")
    public ValidatableResponse createOrder(String token, CreateOrderForm body) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .body(body)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Send GET Request /api/orders")
    public ValidatableResponse getUserOrders(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .get(ORDERS_PATH)
                .then();
    }

}
