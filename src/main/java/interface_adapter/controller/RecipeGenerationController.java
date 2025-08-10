package interface_adapter.controller;

import entity.Diet;
import use_case.recipe_generation.RecipeGenerationRequestModel;
import use_case.recipe_generation.RecipeGenerationUseCase;

public class RecipeGenerationController {
    private final RecipeGenerationUseCase useCase;

    public RecipeGenerationController(RecipeGenerationUseCase useCase) { this.useCase = useCase; }

    public void search(String query, String mealType, String cuisine, String dietLabel, int topN) {
        var req = new RecipeGenerationRequestModel(
                query, mealType, cuisine, Diet.fromLabel(dietLabel), topN
        );
        useCase.generate(req);
    }
}
