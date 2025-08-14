package use_case.recipe;

import use_case.gateway.RecipeGateway;

import java.util.List;

public class ListCuisinesInteractor implements ListCuisinesUseCase {
    private final RecipeGateway gateway;

    public ListCuisinesInteractor(RecipeGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public List<String> execute() throws Exception {
        return gateway.listCuisines();
    }
}
