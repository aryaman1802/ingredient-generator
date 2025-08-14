package interface_adapter.controller;

import use_case.random_recipe.RandomRecipeUseCase;

public class RandomRecipeController {
    private final RandomRecipeUseCase useCase;

    public RandomRecipeController(RandomRecipeUseCase useCase) { this.useCase = useCase; }

    public void surpriseMe() {
        useCase.fetch();
    }
}
