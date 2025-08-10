package use_case.random_recipe;

public interface RandomRecipeOutputBoundary {
    void present(RandomRecipeResponseModel response);
    void fail(String errorMessage);
}
