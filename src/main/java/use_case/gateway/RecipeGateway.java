package use_case.gateway;

import java.util.List;
import entity.Recipe;

/**
 * A gateway interface for fetching recipes.
 */
public interface RecipeGateway {
    /**
     * Fetch a list of recipes matching the query + filters.
     *
     * @param query       comma-separated ingredients or search term
     * @param mealType    e.g. "Breakfast", "Dinner", etc.
     * @param cuisineType e.g. "Indian", "Italian", etc.
     * @return            a list of Recipe entities
     * @throws Exception  on network / parsing error
     */
    List<Recipe> fetchRecipes(String query,
                              String mealType,
                              String cuisineType) throws Exception;
}
