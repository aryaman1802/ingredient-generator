package use_case.recipe_generation;

public interface RecipeGenerationOutputBoundary {
    void present(RecipeGenerationResponseModel response);
    void fail(String errorMessage);
}
