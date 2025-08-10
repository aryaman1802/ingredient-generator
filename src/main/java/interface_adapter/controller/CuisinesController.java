package interface_adapter.controller;

import use_case.cuisines.ListCuisinesUseCase;

public class CuisinesController {
    private final ListCuisinesUseCase useCase;

    public CuisinesController(ListCuisinesUseCase useCase) { this.useCase = useCase; }

    public void loadCuisines() { useCase.load(); }
}
