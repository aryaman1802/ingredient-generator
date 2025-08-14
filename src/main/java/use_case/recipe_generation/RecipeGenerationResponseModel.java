package use_case.recipe_generation;

import entity.RecipeDB;

import java.util.List;

public class RecipeGenerationResponseModel {
    private final List<RecipeDB> recipes;
    private final String message;

    public RecipeGenerationResponseModel(List<RecipeDB> recipes, String message) {
        this.recipes = recipes;
        this.message = message;
    }

    public List<RecipeDB> getRecipes() { return recipes; }
    public String getMessage() { return message; }
}
