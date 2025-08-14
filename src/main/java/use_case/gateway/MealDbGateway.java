package use_case.gateway;

import entity.RecipeDB;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MealDbGateway {
    List<RecipeDB> searchByName(String query) throws Exception;           // full details
    List<RecipeDB> searchByFirstLetter(String letter) throws Exception;   // full details
    Set<String> filterByIngredient(String ingredient) throws Exception; // ids
    Set<String> filterByArea(String area) throws Exception;             // ids
    Set<String> filterByCategory(String category) throws Exception;     // ids
    Optional<RecipeDB> lookupById(String id) throws Exception;            // full details
    RecipeDB random() throws Exception;                                   // full details
    List<String> listAreas() throws Exception;                          // cuisines/areas
}
