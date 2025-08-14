//package use_case.gateway;
//
//import java.util.List;
//import entity.Recipe;
//
///**
// * A gateway interface for fetching recipes.
// */
//public interface RecipeGateway {
//    /**
//     * Fetch a list of recipes matching the query + filters.
//     *
//     * @param query       comma-separated ingredients or search term
//     * @param mealType    e.g. "Breakfast", "Dinner", etc.
//     * @param cuisineType e.g. "Indian", "Italian", etc.
//     * @return            a list of Recipe entities
//     * @throws Exception  on network / parsing error
//     */
//    List<Recipe> fetchRecipes(String query,
//                              String mealType,
//                              String cuisineType) throws Exception;
//}



package use_case.gateway;

import entity.Recipe;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface RecipeGateway {
    // Existing
    List<String> listCuisines() throws Exception;
    void writePreferences(String query, String mealType, String cuisine) throws IOException;
    void runDemo3() throws Exception;
    List<Recipe> readAndParseRecipes(Path path) throws IOException;
    Recipe fetchRandomRecipe() throws Exception;

    // NEW for Demo3’s search workflow
    List<Recipe> searchByName(String query) throws Exception;             // full details
    List<Recipe> searchByFirstLetter(String letter) throws Exception;     // full details
    Set<String>  filterByIngredient(String ingredient) throws Exception;  // ids only
    Set<String>  filterByArea(String area) throws Exception;              // ids only
    Set<String>  filterByCategory(String category) throws Exception;      // ids only
    Recipe       lookupById(String id) throws Exception;                  // full details
}
