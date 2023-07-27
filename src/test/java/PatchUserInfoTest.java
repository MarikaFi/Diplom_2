import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import client.Client;
import generator.ClientGenerator;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import form.PatchForm;

public class PatchUserInfoTest {
    private Client authClient;
    private String token;
    private final String email = RandomStringUtils.randomAlphabetic(10) + "@mail.com";
    private final String NAME = "NewTestName";

    @Before
    public void setUp() {
        authClient = new Client();
        ValidatableResponse response = authClient.createUser(new ClientGenerator().getRandomCreateUserForm());
        token = response.extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        if (token != null) {
            authClient.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Изменение информации для авторизованного пользователя")
    public void patchInfoByAuthUser() {
        ValidatableResponse response = authClient.patchUserInfo(token, new PatchForm(email, NAME));
        checkPositiveResponse(response);
    }

    @Test
    @DisplayName("Изменение информации для НЕ авторизованного пользователя")
    public void patchInfoByUser() {
        ValidatableResponse response = authClient.patchUserInfo("default", new PatchForm(email, NAME));
        checkNegativeResponse(response);
    }

    @Test
    @DisplayName("Изменение информации с почтой, которая уже используется")
    public void patchInfoByUserWithRepeatEmail() {
        String emailFail = "Test@yandex.ru";
        PatchForm body = new PatchForm(emailFail, NAME);
        ValidatableResponse response = authClient.patchUserInfo(token, body);
        checkResponseWithDublicate(response);
    }

    @Step("Проверка ответа метода")
    private void checkResponseWithDublicate(ValidatableResponse response) {
        String expectedMessage = "User with such email already exists";
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_FORBIDDEN, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expectedMessage, response.extract().path("message"));
    }

    @Step("Проверка ответа метода")
    private void checkPositiveResponse(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(email.toLowerCase(), response.extract().path("user.email"));
        Assert.assertEquals(NAME, response.extract().path("user.name"));
    }

    @Step("Проверка ответа метода")
    private void checkNegativeResponse(ValidatableResponse response) {
        String expectedMessage = "You should be authorised";
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_UNAUTHORIZED, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expectedMessage, response.extract().path("message"));
    }

}
