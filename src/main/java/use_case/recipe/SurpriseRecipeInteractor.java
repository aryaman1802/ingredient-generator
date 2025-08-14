package use_case.recipe;

import entity.Recipe;
import use_case.gateway.RecipeGateway;

public class SurpriseRecipeInteractor implements SurpriseRecipeUseCase {
    private final RecipeGateway gateway;

    public SurpriseRecipeInteractor(RecipeGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Recipe execute() throws Exception {
        return gateway.fetchRandomRecipe();
    }
}
