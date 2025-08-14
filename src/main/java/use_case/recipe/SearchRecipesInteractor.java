package use_case.recipe;

import entity.Diet;
import entity.Recipe;
import use_case.gateway.RecipeGateway;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SearchRecipesInteractor implements SearchRecipesUseCase {

    private final RecipeGateway gateway;

    public SearchRecipesInteractor(RecipeGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public List<Recipe> execute(String query, String mealType, String cuisine, String dietLabel) throws Exception {
        gateway.writePreferences(query, mealType, cuisine); // 3 lines as per your Demo3
        gateway.runDemo3(); // writes recipes.txt
        List<Recipe> all = gateway.readAndParseRecipes(Paths.get("recipes.txt"));

        Diet desired = Diet.fromLabel(dietLabel);
        List<Recipe> filtered = filterByDiet(all, desired);
        if (filtered.size() > 3) return new ArrayList<>(filtered.subList(0, 3));
        return filtered;
    }

    private List<Recipe> filterByDiet(List<Recipe> all, Diet desired) {
        if (desired == Diet.NONE) return all;
        List<Recipe> out = new ArrayList<>();
        for (Recipe r : all) {
            switch (desired) {
                case VEGETARIAN:
                    if (r.diet == Diet.VEGETARIAN || r.diet == Diet.VEGAN) out.add(r);
                    break;
                case VEGAN:
                    if (r.diet == Diet.VEGAN) out.add(r);
                    break;
                case NON_VEGETARIAN:
                    if (r.diet == Diet.NON_VEGETARIAN) out.add(r);
                    break;
                default:
                    out.add(r);
            }
        }
        return out;
    }
}
