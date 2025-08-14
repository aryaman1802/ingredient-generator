package interface_adapter.controller;

import entity.Recipe;
import use_case.recipe.ListCuisinesUseCase;
import use_case.recipe.SearchRecipesUseCase;
import use_case.recipe.SurpriseRecipeUseCase;

import java.util.List;

public class RecipesController {
    private final SearchRecipesUseCase searchUC;
    private final SurpriseRecipeUseCase surpriseUC;
    private final ListCuisinesUseCase cuisinesUC;

    public RecipesController(SearchRecipesUseCase searchUC,
                             SurpriseRecipeUseCase surpriseUC,
                             ListCuisinesUseCase cuisinesUC) {
        this.searchUC = searchUC;
        this.surpriseUC = surpriseUC;
        this.cuisinesUC = cuisinesUC;
    }

    public List<String> listCuisines() throws Exception {
        return cuisinesUC.execute();
    }

    public List<Recipe> search(String query, String mealType, String cuisine, String dietLabel) throws Exception {
        return searchUC.execute(query, mealType, cuisine, dietLabel);
    }

    public Recipe surprise() throws Exception {
        return surpriseUC.execute();
    }
}
