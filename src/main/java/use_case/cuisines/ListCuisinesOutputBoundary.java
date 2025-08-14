package use_case.cuisines;

public interface ListCuisinesOutputBoundary {
    void present(ListCuisinesResponseModel response);
    void fail(String errorMessage);
}

