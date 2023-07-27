import static org.apache.http.HttpStatus.SC_FORBIDDEN;
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

public class CreateNewUserTest {
    private Client сlient;
    private String token;

    @Before
    public void setUp() {
        сlient = new Client();
    }

    @After
    public void cleanUp() {
        if (token != null) {
            сlient.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Успешное создание УЗ")
    public void positiveCreateUser() {
        CreateUserForm createUserForm = new ClientGenerator().getRandomCreateUserForm();
        ValidatableResponse response = сlient.createUser(createUserForm);
        checkPositiveResponse(response, createUserForm);
    }

    @Test
    @DisplayName("Запрос без параметра: Email")
    public void createUserWithoutEmail() {
        ValidatableResponse response = сlient.createUser(new ClientGenerator()
                .getRandomCreateUserFormWithout("email"));
        checkResponseWithoutField(response);
    }

    @Test
    @DisplayName("Запрос без параметра: Password")
    public void createUserWithoutPassword() {
        ValidatableResponse response = сlient.createUser(new ClientGenerator()
                .getRandomCreateUserFormWithout("password"));
        checkResponseWithoutField(response);
    }

    @Test
    @DisplayName("Запрос без параметра: Name")
    public void createUserWithoutName() {
        ValidatableResponse response = сlient.createUser(new ClientGenerator()
                .getRandomCreateUserFormWithout("name"));
        checkResponseWithoutField(response);
    }

    @Test
    @DisplayName("Создание существующего пользователя")
    public void createUserWithDublicateFields() {
        CreateUserForm createUserForm = new ClientGenerator().getRandomCreateUserForm();
        сlient.createUser(createUserForm);
        ValidatableResponse response = сlient.createUser(createUserForm);
        checkResponseWithDublicateFields(response);
    }

    @Step("Проверка ответа метода")
    private void checkResponseWithDublicateFields(ValidatableResponse response) {
        String expected = "User already exists";
        Boolean success = response.extract().path("success");
        Assert.assertEquals(SC_FORBIDDEN, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expected, response.extract().path("message"));
    }

    @Step("Проверка ответа метода")
    private void checkPositiveResponse(ValidatableResponse response, CreateUserForm createUserForm) {
        Boolean success = response.extract().path("success");
        token = response.extract().path("accessToken");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(createUserForm.getEmail().toLowerCase(), response.extract().path("user.email"));
        Assert.assertEquals(createUserForm.getName(), response.extract().path("user.name"));
        Assert.assertNotNull(token);
        Assert.assertNotNull(response.extract().path("refreshToken"));
    }

    @Step("Проверка ответа метода")
    private void checkResponseWithoutField(ValidatableResponse response) {
        String expected = "Email, password and name are required fields";
        Boolean success = response.extract().path("success");
        Assert.assertEquals(SC_FORBIDDEN, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expected, response.extract().path("message"));
    }

}
