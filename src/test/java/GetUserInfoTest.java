import static org.apache.http.HttpStatus.SC_OK;

import client.Client;
import generator.ClientGenerator;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import form.CreateUserForm;

public class GetUserInfoTest {
    private Client client;
    private CreateUserForm loginDetails;
    private String token;

    @Before
    public void setUp() {
        client = new Client();
        loginDetails = new ClientGenerator().getRandomCreateUserForm();
        ValidatableResponse response = client.createUser(loginDetails);
        token = response.extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        if (token != null) {
            client.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Получение информации об авторизованном пользователе")
    public void getInfoByAuthUser() {
        ValidatableResponse response = client.getUserInfo(token);
        checkPositiveResponse(response);
    }

    @Step("Проверка ответа метода")
    private void checkPositiveResponse(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(loginDetails.getEmail().toLowerCase(), response.extract().path("user.email"));
        Assert.assertEquals(loginDetails.getName(), response.extract().path("user.name"));
    }
}
