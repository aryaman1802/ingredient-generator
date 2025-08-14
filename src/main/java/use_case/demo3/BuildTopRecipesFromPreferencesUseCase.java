package use_case.demo3;

import entity.Recipe;
import java.util.List;

public interface BuildTopRecipesFromPreferencesUseCase {
    List<Recipe> execute(String query, String mealType, String cuisine) throws Exception;
}
