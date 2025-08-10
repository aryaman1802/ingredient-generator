package use_case.recipe_generation;

import entity.Recipe;
import java.util.List;

/**
 * Response model for recipe generation use case.
 * Contains the result of recipe generation and any error messages.
 */
public class RecipeGenerationResponseModel {
    private final boolean success;
    private final String message;
    private final List<Recipe> recipes;

    public RecipeGenerationResponseModel(boolean success, String message, List<Recipe> recipes) {
        this.success = success;
        this.message = message;
        this.recipes = recipes;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }
}