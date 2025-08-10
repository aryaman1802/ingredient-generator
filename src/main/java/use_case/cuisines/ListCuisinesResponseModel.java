package use_case.cuisines;

import java.util.List;

public class ListCuisinesResponseModel {
    private final List<String> areas;

    public ListCuisinesResponseModel(List<String> areas) { this.areas = areas; }
    public List<String> getAreas() { return areas; }
}

