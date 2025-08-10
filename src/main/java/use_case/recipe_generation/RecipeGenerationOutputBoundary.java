package use_case.recipe_generation;

/**
 * Output boundary interface for recipe generation use case.
 * Defines how the interactor communicates results back to the presenter.
 */
public interface RecipeGenerationOutputBoundary {
    /**
     * Present the result of recipe generation.
     * @param response the response containing recipes or error information
     */
    void present(RecipeGenerationResponseModel response);
}