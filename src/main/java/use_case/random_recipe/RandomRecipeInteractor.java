package use_case.random_recipe;

import use_case.gateway.MealDbGateway;

public class RandomRecipeInteractor implements RandomRecipeUseCase {
    private final MealDbGateway gateway;
    private final RandomRecipeOutputBoundary presenter;

    public RandomRecipeInteractor(MealDbGateway gateway, RandomRecipeOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void fetch() {
        try {
            var recipe = gateway.random();
            presenter.present(new RandomRecipeResponseModel(recipe));
        } catch (Exception e) {
            presenter.fail("Random recipe fetch failed: " + e.getMessage());
        }
    }
}

