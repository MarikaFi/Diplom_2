import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import client.Client;
import client.OrdersClient;
import generator.ClientGenerator;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import form.CreateOrderForm;

public class GetUserOrdersTest {
    private OrdersClient ordersClient;
    private String token;
    private final String[] INGREDIENTS = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"};
    private String expectedId;
    private String expectedStatus;
    private String expectedName;
    private String expectedCreatedAt;
    private String expectedUpdatedAt;
    private String expectedNumber;

    @Before
    public void setUp() {
        ordersClient = new OrdersClient();
        token = new Client().createUser(new ClientGenerator().getRandomCreateUserForm())
                .extract()
                .path("accessToken");
        ValidatableResponse response = ordersClient.createOrder(token, new CreateOrderForm(INGREDIENTS));
        expectedId = response.extract().path("order._id");
        expectedStatus = response.extract().path("order.status");
        expectedName = response.extract().path("order.name");
        expectedCreatedAt = response.extract().path("order.createdAt");
        expectedUpdatedAt = response.extract().path("order.updatedAt");
        expectedNumber = response.extract().path("order.number").toString();
    }

    @After
    public void cleanUp() {
        if (token != null) {
            new Client().deleteUser(token);
        }
    }

    @Test
    @DisplayName("Получение списка заказов авторизованным пользователем")
    public void getOrderPositive() {
        ValidatableResponse response = ordersClient.getUserOrders(token);
        checkResponse(response);
    }

    @Test
    @DisplayName("Получение списка заказов НЕ авторизованным пользователем")
    public void getOrderNegative() {
        ValidatableResponse response = ordersClient.getUserOrders("default");
        checkNegativeResponse(response);
    }

    @Step("Проверка ответа метода")
    private void checkNegativeResponse(ValidatableResponse response) {
        String expectedMessage = "You should be authorised";
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_UNAUTHORIZED, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
    }

    @Step("Проверка ответа метода")
    private void checkResponse(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        String actualId = response.extract().path("orders._id").toString();
        String actualStatus = response.extract().path("orders.status").toString();
        String actualName = response.extract().path("orders.name").toString();
        String actualCreated = response.extract().path("orders.createdAt").toString();
        String actualUpdated = response.extract().path("orders.updatedAt").toString();
        String actualNumber = response.extract().path("orders.number").toString();

        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(expectedId, actualId.substring(1, actualId.length() - 1));
        Assert.assertEquals(expectedStatus, actualStatus.substring(1, actualStatus.length() - 1));
        Assert.assertEquals(expectedName, actualName.substring(1, actualName.length() - 1));
        Assert.assertEquals(expectedCreatedAt, actualCreated.substring(1, actualCreated.length() - 1));
        Assert.assertEquals(expectedUpdatedAt, actualUpdated.substring(1, actualUpdated.length() - 1));
        Assert.assertEquals(expectedNumber, actualNumber.substring(1, actualNumber.length() - 1));
    }

}
