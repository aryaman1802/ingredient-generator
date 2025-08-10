package interface_adapter.presenter;

import use_case.random_recipe.RandomRecipeOutputBoundary;
import use_case.random_recipe.RandomRecipeResponseModel;
import view.RecipeSearchView;

public class RandomRecipePresenter implements RandomRecipeOutputBoundary {
    private final RecipeSearchView view;

    public RandomRecipePresenter(RecipeSearchView view) { this.view = view; }

    @Override
    public void present(RandomRecipeResponseModel response) {
        view.showRandom(response.getRecipe());
    }

    @Override
    public void fail(String errorMessage) {
        view.showError(errorMessage);
    }
}
