package use_case.recipe_history;

/**
 * Output boundary interface for recipe history use case.
 * Defines how the interactor communicates results back to the presenter.
 */
public interface RecipeHistoryOutputBoundary {
    /**
     * Present the result of recipe history operations.
     * @param response the response containing history data or error information
     */
    void present(RecipeHistoryResponseModel response);
}