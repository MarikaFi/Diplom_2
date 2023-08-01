package form;
import java.util.Arrays;
public class CreateOrderForm {
    private String[] ingredients;

    public CreateOrderForm(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public CreateOrderForm() {
    }

    @Override
    public String toString() {
        return "ingredients: " + Arrays.toString(ingredients);
    }
}
