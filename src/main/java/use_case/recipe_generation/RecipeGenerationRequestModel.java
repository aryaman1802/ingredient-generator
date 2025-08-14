package use_case.recipe_generation;

import entity.Diet;

public class RecipeGenerationRequestModel {
    private final String query;
    private final String mealType;  // "Breakfast", "Lunch", "Dinner", or "Any"
    private final String cuisine;   // "Any" or an area
    private final Diet dietFilter;  // NONE, VEG, NON-VEG, VEGAN
    private final int topN;

    public RecipeGenerationRequestModel(String query, String mealType, String cuisine, Diet dietFilter, int topN) {
        this.query = query;
        this.mealType = mealType;
        this.cuisine = cuisine;
        this.dietFilter = dietFilter;
        this.topN = topN <= 0 ? 3 : topN;
    }

    public String getQuery() { return query; }
    public String getMealType() { return mealType; }
    public String getCuisine() { return cuisine; }
    public entity.Diet getDietFilter() { return dietFilter; }
    public int getTopN() { return topN; }
}
