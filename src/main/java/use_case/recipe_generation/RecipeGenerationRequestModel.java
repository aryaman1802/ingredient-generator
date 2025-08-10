package use_case.recipe_generation;

/**
 * Request model for recipe generation use case.
 * Contains user preferences for ingredients, meal type, and cuisine type.
 */
public class RecipeGenerationRequestModel {
    private final String ingredients;
    private final String mealType;
    private final String cuisineType;

    public RecipeGenerationRequestModel(String ingredients, String mealType, String cuisineType) {
        this.ingredients = ingredients;
        this.mealType = mealType;
        this.cuisineType = cuisineType;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getMealType() {
        return mealType;
    }

    public String getCuisineType() {
        return cuisineType;
    }
}