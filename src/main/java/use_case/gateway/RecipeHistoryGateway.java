package use_case.gateway;

import entity.RegularUser;
import entity.Recipe;
import java.util.List;

/**
 * Gateway interface for recipe history persistence.
 * Abstracts the data access layer for recipe history operations.
 */
public interface RecipeHistoryGateway {
    /**
     * Save recipes to user's history.
     * @param user the user whose history to update
     * @param recipes the recipes to save
     * @throws Exception on persistence error
     */
    void saveRecipeHistory(RegularUser user, List<Recipe> recipes) throws Exception;

    /**
     * Retrieve user's recipe history.
     * @param user the user whose history to retrieve
     * @return list of recipes from user's history
     * @throws Exception on retrieval error
     */
    List<Recipe> getRecipeHistory(RegularUser user) throws Exception;

    /**
     * Get the count of recipes in user's history.
     * @param user the user whose history count to get
     * @return number of recipe entries in history
     * @throws Exception on retrieval error
     */
    int getHistoryCount(RegularUser user) throws Exception;
}