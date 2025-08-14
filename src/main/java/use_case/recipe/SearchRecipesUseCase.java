package use_case.recipe;

import entity.Recipe;
import java.util.List;

public interface SearchRecipesUseCase {
    List<Recipe> execute(String query, String mealType, String cuisine, String dietLabel) throws Exception;
}

