package use_case.recipe_history;

import entity.Recipe;
import use_case.gateway.RecipeHistoryGateway;
import java.util.List;

/**
 * Interactor for the Recipe History use case.
 * Implements the business logic for managing user recipe history.
 */
public class RecipeHistoryInteractor implements RecipeHistoryUseCase {
    private final RecipeHistoryGateway historyGateway;
    private final RecipeHistoryOutputBoundary presenter;

    public RecipeHistoryInteractor(RecipeHistoryGateway historyGateway,
                                   RecipeHistoryOutputBoundary presenter) {
        this.historyGateway = historyGateway;
        this.presenter = presenter;
    }

    @Override
    public void processHistory(RecipeHistoryRequestModel request) {
        try {
            if (request.getUser() == null) {
                presenter.present(new RecipeHistoryResponseModel(
                    false,
                    "User cannot be null",
                    null,
                    0
                ));
                return;
            }

            switch (request.getOperation().toLowerCase()) {
                case "save":
                    handleSaveOperation(request);
                    break;
                case "retrieve":
                    handleRetrieveOperation(request);
                    break;
                default:
                    presenter.present(new RecipeHistoryResponseModel(
                        false,
                        "Invalid operation: " + request.getOperation(),
                        null,
                        0
                    ));
            }
        } catch (Exception e) {
            presenter.present(new RecipeHistoryResponseModel(
                false,
                "History operation failed: " + e.getMessage(),
                null,
                0
            ));
        }
    }

    private void handleSaveOperation(RecipeHistoryRequestModel request) throws Exception {
        if (request.getRecipes() == null || request.getRecipes().isEmpty()) {
            presenter.present(new RecipeHistoryResponseModel(
                false,
                "No recipes to save",
                null,
                0
            ));
            return;
        }

        historyGateway.saveRecipeHistory(request.getUser(), request.getRecipes());
        int newCount = historyGateway.getHistoryCount(request.getUser());
        
        presenter.present(new RecipeHistoryResponseModel(
            true,
            "Recipes saved to history successfully",
            request.getRecipes(),
            newCount
        ));
    }

    private void handleRetrieveOperation(RecipeHistoryRequestModel request) throws Exception {
        List<Recipe> historyRecipes = historyGateway.getRecipeHistory(request.getUser());
        int count = historyGateway.getHistoryCount(request.getUser());

        if (historyRecipes.isEmpty()) {
            presenter.present(new RecipeHistoryResponseModel(
                true,
                "No recipe history found",
                historyRecipes,
                count
            ));
        } else {
            presenter.present(new RecipeHistoryResponseModel(
                true,
                "Recipe history retrieved successfully",
                historyRecipes,
                count
            ));
        }
    }
}