package use_case.recipe_history;

import entity.RegularUser;
import entity.Recipe;
import java.util.List;

/**
 * Request model for recipe history use case.
 * Contains user information and recipes to save or retrieve.
 */
public class RecipeHistoryRequestModel {
    private final RegularUser user;
    private final List<Recipe> recipes;
    private final String operation; // "save" or "retrieve"

    public RecipeHistoryRequestModel(RegularUser user, List<Recipe> recipes, String operation) {
        this.user = user;
        this.recipes = recipes;
        this.operation = operation;
    }

    public RegularUser getUser() {
        return user;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public String getOperation() {
        return operation;
    }
}