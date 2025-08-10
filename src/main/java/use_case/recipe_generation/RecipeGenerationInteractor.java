package use_case.recipe_generation;

import entity.Recipe;
import use_case.gateway.RecipeGateway;
import java.util.List;

/**
 * Interactor for the Recipe Generation use case.
 * Implements the business logic for generating recipes based on user preferences.
 */
public class RecipeGenerationInteractor implements RecipeGenerationUseCase {
    private final RecipeGateway recipeGateway;
    private final RecipeGenerationOutputBoundary presenter;

    public RecipeGenerationInteractor(RecipeGateway recipeGateway, 
                                      RecipeGenerationOutputBoundary presenter) {
        this.recipeGateway = recipeGateway;
        this.presenter = presenter;
    }

    @Override
    public void generateRecipes(RecipeGenerationRequestModel request) {
        try {
            // Validate input
            if (request.getIngredients() == null || request.getIngredients().trim().isEmpty()) {
                presenter.present(new RecipeGenerationResponseModel(
                    false, 
                    "Ingredients cannot be empty", 
                    null
                ));
                return;
            }

            // Format ingredients for API call
            String formattedIngredients = formatIngredients(request.getIngredients());
            
            // Fetch recipes from external API
            List<Recipe> recipes = recipeGateway.fetchRecipes(
                formattedIngredients,
                request.getMealType(),
                request.getCuisineType()
            );

            if (recipes.isEmpty()) {
                presenter.present(new RecipeGenerationResponseModel(
                    false,
                    "No recipes found matching your preferences",
                    null
                ));
            } else {
                presenter.present(new RecipeGenerationResponseModel(
                    true,
                    "Recipes generated successfully",
                    recipes
                ));
            }
        } catch (Exception e) {
            presenter.present(new RecipeGenerationResponseModel(
                false,
                "Failed to generate recipes: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * Format ingredients string for API consumption.
     * Remove extra spaces and ensure comma separation.
     */
    private String formatIngredients(String ingredients) {
        if (ingredients == null) {
            return "";
        }
        
        StringBuilder formatted = new StringBuilder();
        String[] parts = ingredients.split(",");
        
        for (int i = 0; i < parts.length; i++) {
            formatted.append(parts[i].trim());
            if (i != parts.length - 1) {
                formatted.append(",");
            }
        }
        
        return formatted.toString();
    }
}