package use_case.recipe_history;

import entity.Recipe;
import java.util.List;

/**
 * Response model for recipe history use case.
 * Contains the result of history operations.
 */
public class RecipeHistoryResponseModel {
    private final boolean success;
    private final String message;
    private final List<Recipe> historyRecipes;
    private final int historyCount;

    public RecipeHistoryResponseModel(boolean success, String message, 
                                      List<Recipe> historyRecipes, int historyCount) {
        this.success = success;
        this.message = message;
        this.historyRecipes = historyRecipes;
        this.historyCount = historyCount;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Recipe> getHistoryRecipes() {
        return historyRecipes;
    }

    public int getHistoryCount() {
        return historyCount;
    }
}