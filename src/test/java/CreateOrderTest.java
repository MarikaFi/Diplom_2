import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;

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

public class CreateOrderTest {
    private OrdersClient ordersClient;
    private String token;
    private final String[] INGREDIENTS = {"640880d09ed280001b2e91b1", "61c0c5a71d1f82001bdaaa71"};
    private final String BURGER_NAME = "Био-марсианский бургер";

    @Before
    public void setUp() {
        ordersClient = new OrdersClient();
        token = new Client().createUser(new ClientGenerator().getRandomCreateUserForm())
                .extract()
                .path("accessToken");
    }

    @After
    public void cleanUp() {
        if (token != null) {
            new Client().deleteUser(token);
        }
    }

    @Test
    @DisplayName("Создание заказа неавторизованным пользователем")
    public void createOrderWithoutAuth() {
        ValidatableResponse response = ordersClient.createOrder(new CreateOrderForm(INGREDIENTS));
        checkResponse(response);
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем")
    public void createOrderWithAuth() {
        ValidatableResponse response = ordersClient.createOrder(token, new CreateOrderForm(INGREDIENTS));
        checkResponseWithAuth(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        ValidatableResponse response = ordersClient.createOrder(new CreateOrderForm(null));
        checkResponseWithoutIngredients(response);
    }

    @Test
    @DisplayName("Создание заказа c невалидным хешем ингредиента")
    public void createOrderWithBadIngredients() {
        ValidatableResponse response = ordersClient.createOrder(new CreateOrderForm(new String[]{"123"}));
        checkResponseBadIngredients(response);
    }

    @Step("Проверка ответа метода")
    private void checkResponseBadIngredients(ValidatableResponse response) {
        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, response.extract().statusCode());
    }

    @Step("Проверка ответа метода")
    private void checkResponseWithoutIngredients(ValidatableResponse response) {
        String expectedMessage = "Ingredient ids must be provided";
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_BAD_REQUEST, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expectedMessage, response.extract().path("message"));
    }

    @Step("Проверка ответа метода")
    private void checkResponseWithAuth(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        ValidatableResponse getUserInfo = new Client().getUserInfo(token);
        String ownerName = getUserInfo.extract().path("user.name");
        String ownerEmail = getUserInfo.extract().path("user.email");

        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(BURGER_NAME, response.extract().path("name"));
        Assert.assertNotNull(response.extract().path("order.number"));
        Assert.assertEquals(ownerName, response.extract().path("order.owner.name"));
        Assert.assertEquals(ownerEmail, response.extract().path("order.owner.email"));
        Assert.assertNotNull(response.extract().path("order.owner.createdAt"));
        Assert.assertNotNull(response.extract().path("order.owner.updatedAt"));
        Assert.assertNotNull(response.extract().path("order.status"));
        Assert.assertEquals(BURGER_NAME, response.extract().path("order.name"));
        Assert.assertNotNull(response.extract().path("order.createdAt"));
        Assert.assertNotNull(response.extract().path("order.createdAt"));
        Assert.assertNotNull(response.extract().path("order.number"));
        Assert.assertNotNull(response.extract().path("order.price"));
    }

    @Step("Проверка ответа метода")
    private void checkResponse(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(BURGER_NAME, response.extract().path("name"));
        Assert.assertNotNull(response.extract().path("order.number"));
    }
}
