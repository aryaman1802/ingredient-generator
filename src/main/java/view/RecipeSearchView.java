package view;

import entity.RecipeDB;

import java.util.List;

public interface RecipeSearchView {
    void populateCuisines(List<String> areas);
    void setStatus(String text);

    void showResults(List<RecipeDB> recipes);
    void showRandom(RecipeDB recipe);
    void showEmpty(String message);
    void showError(String message);
}
