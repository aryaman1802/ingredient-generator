package interface_adapter.presenter;

import entity.RecipeDB;
import use_case.recipe_generation.RecipeGenerationOutputBoundary;
import use_case.recipe_generation.RecipeGenerationResponseModel;
import view.RecipeSearchView;

import java.util.List;

public class RecipeGenerationPresenter implements RecipeGenerationOutputBoundary {
    private final RecipeSearchView view;

    public RecipeGenerationPresenter(RecipeSearchView view) { this.view = view; }

    @Override
    public void present(RecipeGenerationResponseModel response) {
        List<RecipeDB> recipes = response.getRecipes();
        if (recipes == null || recipes.isEmpty()) {
            view.showEmpty("No recipes matched your criteria.");
        } else {
            view.showResults(recipes);
        }
    }

    @Override
    public void fail(String errorMessage) {
        view.showError(errorMessage);
    }
}
