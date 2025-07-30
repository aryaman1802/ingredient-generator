package entity;

import java.util.List;

public class Recipe {
    private final String label;
    private final List<String> ingredientLines;

    public Recipe(String label, List<String> ingredientLines) {
        this.label = label;
        this.ingredientLines = List.copyOf(ingredientLines);
    }

    public String getLabel() {
        return label;
    }

    public List<String> getIngredientLines() {
        return ingredientLines;
    }
}
