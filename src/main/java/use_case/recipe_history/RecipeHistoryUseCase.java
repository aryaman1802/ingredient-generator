package use_case.recipe_history;

/**
 * Use case interface for recipe history management.
 * Defines the contract for saving and retrieving user recipe history.
 */
public interface RecipeHistoryUseCase {
    /**
     * Process recipe history operations (save or retrieve).
     * @param request the request containing user and recipe information
     */
    void processHistory(RecipeHistoryRequestModel request);
}