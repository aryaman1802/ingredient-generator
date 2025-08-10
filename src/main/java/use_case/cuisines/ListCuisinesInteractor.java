package use_case.cuisines;

import use_case.gateway.MealDbGateway;

import java.util.ArrayList;
import java.util.List;

public class ListCuisinesInteractor implements ListCuisinesUseCase {
    private final MealDbGateway gateway;
    private final ListCuisinesOutputBoundary presenter;

    public ListCuisinesInteractor(MealDbGateway gateway, ListCuisinesOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void load() {
        try {
            List<String> areas = gateway.listAreas();
            if (areas == null || areas.isEmpty()) {
                areas = fallback();
            }
            presenter.present(new ListCuisinesResponseModel(areas));
        } catch (Exception e) {
            presenter.present(new ListCuisinesResponseModel(fallback()));
        }
    }

    private static List<String> fallback() {
        return new ArrayList<>(List.of(
                "Any","American","British","Canadian","Chinese","Dutch","Egyptian","French","Greek",
                "Indian","Irish","Italian","Jamaican","Japanese","Kenyan","Malaysian","Mexican","Moroccan",
                "Polish","Portuguese","Russian","Spanish","Thai","Tunisian","Turkish","Vietnamese"
        ));
    }
}
