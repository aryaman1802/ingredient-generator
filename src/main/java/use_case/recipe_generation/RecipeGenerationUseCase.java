package use_case.recipe_generation;

/**
 * Use case interface for recipe generation.
 * Defines the contract for generating recipes based on user preferences.
 */
public interface RecipeGenerationUseCase {
    /**
     * Generate recipes based on user preferences.
     * @param request the request containing user preferences
     */
    void generateRecipes(RecipeGenerationRequestModel request);
}