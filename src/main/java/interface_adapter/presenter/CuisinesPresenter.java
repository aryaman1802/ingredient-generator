package interface_adapter.presenter;

import use_case.cuisines.ListCuisinesOutputBoundary;
import use_case.cuisines.ListCuisinesResponseModel;
import view.RecipeSearchView;

public class CuisinesPresenter implements ListCuisinesOutputBoundary {
    private final RecipeSearchView view;

    public CuisinesPresenter(RecipeSearchView view) { this.view = view; }

    @Override
    public void present(ListCuisinesResponseModel response) {
        view.populateCuisines(response.getAreas());
    }

    @Override
    public void fail(String errorMessage) {
        view.showError(errorMessage);
    }
}
