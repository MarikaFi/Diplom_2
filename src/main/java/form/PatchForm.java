package form;

public class PatchForm {
    private String email;
    private String name;

    public PatchForm() {
    }

    public PatchForm(String email, String name) {
        this.email = email;
        this.name = name;
    }

    @Override
    public String toString() {
        return "email: " + email + ", name: " + name;
    }
}
