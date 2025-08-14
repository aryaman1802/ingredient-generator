package use_case.random_recipe;

import entity.RecipeDB;

public class RandomRecipeResponseModel {
    private final RecipeDB recipe;

    public RandomRecipeResponseModel(RecipeDB recipe) { this.recipe = recipe; }
    public RecipeDB getRecipe() { return recipe; }
}
