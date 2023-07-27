package generator;

import org.apache.commons.lang3.RandomStringUtils;
import form.PatchForm;
import form.LoginForm;
import form.CreateUserForm;

public class ClientGenerator {
    private final String field = RandomStringUtils.randomAlphabetic(10);

    public CreateUserForm getRandomCreateUserForm() {
        return new CreateUserForm(field + "@yandex.ru", field, field);
    }

    public CreateUserForm getRandomCreateUserFormWithout(String name) {
        CreateUserForm createUserForm = new CreateUserForm();
        switch (name) {
            case "email":
                createUserForm.setPassword(field);
                createUserForm.setName(field);
                break;
            case "password":
                createUserForm.setEmail(field + "@yandex.ru");
                createUserForm.setName(field);
                break;
            case "name":
                createUserForm.setEmail(field + "@yandex.ru");
                createUserForm.setPassword(field);
                break;
        }
        return createUserForm;
    }

    public LoginForm getLoginFormBy(CreateUserForm createUserForm) {
        return new LoginForm(createUserForm.getEmail(), createUserForm.getPassword());
    }

    public LoginForm getLoginFormWithIncorrect(CreateUserForm createUserForm, String value) {
        LoginForm loginForm = new LoginForm();
        if (value.equals("email")) {
            loginForm.setEmail(createUserForm.getEmail() + field);
            loginForm.setPassword(createUserForm.getPassword());
        } else if (value.equals("password")) {
            loginForm.setEmail(createUserForm.getEmail());
            loginForm.setPassword(createUserForm.getPassword() + field);
        }
        return loginForm;
    }

    public PatchForm getPatchForm(String email, String name) {
        return new PatchForm(email, name);
    }

}
