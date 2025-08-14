package use_case.recipe_generation;

import entity.Diet;
import entity.DietRules;
import entity.RecipeDB;
import use_case.gateway.MealDbGateway;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeGenerationInteractor implements RecipeGenerationUseCase {
    private final MealDbGateway gateway;
    private final RecipeGenerationOutputBoundary presenter;

    public RecipeGenerationInteractor(MealDbGateway gateway,
                                      RecipeGenerationOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void generate(RecipeGenerationRequestModel req) {
        try {
            String qValue = safe(req.getQuery());
            String categoryFilter = normalizeCategoryForMealType(req.getMealType());
            Set<String> areaFilters = normalizeAreasForCuisine(req.getCuisine());

            // A) direct name search (full recipes)
            List<RecipeDB> searchMeals = qValue.isEmpty() ? List.of() : gateway.searchByName(qValue);

            // B) ingredient intersection -> ids
            Set<String> candidateIds = new LinkedHashSet<>();
            Set<String> ingredientIds = intersectByIngredients(qValue);
            candidateIds.addAll(extractIds(searchMeals));
            if (!ingredientIds.isEmpty()) {
                if (candidateIds.isEmpty()) candidateIds.addAll(ingredientIds);
                else candidateIds.retainAll(ingredientIds);
            }

            // C1) area filters
            if (!areaFilters.isEmpty()) {
                Set<String> byArea = new LinkedHashSet<>();
                for (String area : areaFilters) byArea.addAll(gateway.filterByArea(area));
                if (!byArea.isEmpty()) {
                    if (candidateIds.isEmpty()) candidateIds.addAll(byArea);
                    else candidateIds.retainAll(byArea);
                }
            }

            // C2) category filter (Breakfast only in TheMealDB)
            if (categoryFilter != null) {
                Set<String> byCat = gateway.filterByCategory(categoryFilter);
                if (!byCat.isEmpty()) {
                    if (candidateIds.isEmpty()) candidateIds.addAll(byCat);
                    else candidateIds.retainAll(byCat);
                }
            }

            // Fallbacks
            if (candidateIds.isEmpty() && !searchMeals.isEmpty()) {
                candidateIds.addAll(extractIds(searchMeals));
            }
            if (candidateIds.isEmpty() && categoryFilter != null) {
                candidateIds.addAll(gateway.filterByCategory(categoryFilter));
            }
            if (candidateIds.isEmpty() && !areaFilters.isEmpty()) {
                for (String area : areaFilters) candidateIds.addAll(gateway.filterByArea(area));
            }
            if (candidateIds.isEmpty() && !qValue.isEmpty() && Character.isLetter(qValue.toLowerCase().charAt(0))) {
                List<RecipeDB> byLetter = gateway.searchByFirstLetter(String.valueOf(qValue.toLowerCase().charAt(0)));
                candidateIds.addAll(extractIds(byLetter));
            }

            if (candidateIds.isEmpty()) {
                presenter.present(new RecipeGenerationResponseModel(List.of(), "No meals found. Try simplifying your query."));
                return;
            }

            // fetch details & score
            List<RecipeDB> detailed = new ArrayList<>();
            for (String id : candidateIds) {
                gateway.lookupById(id).ifPresent(detailed::add);
            }

            // infer diet for each (if not already set by gateway)
            for (RecipeDB r : detailed) {
                if (r.getDiet() == Diet.NONE) {
                    r.setDiet(DietRules.inferDiet(r.getIngredients()));
                }
            }

            List<String> wanted = tokenizeIngredients(qValue);
            detailed.sort((a, b) -> Integer.compare(
                    scoreMeal(b, wanted, categoryFilter, areaFilters),
                    scoreMeal(a, wanted, categoryFilter, areaFilters)
            ));

            // apply diet filter
            List<RecipeDB> filtered = applyDietFilter(detailed, req.getDietFilter());
            if (filtered.size() > req.getTopN()) filtered = filtered.subList(0, req.getTopN());

            presenter.present(new RecipeGenerationResponseModel(filtered, "OK"));
        } catch (Exception ex) {
            presenter.fail("Generation failed: " + ex.getMessage());
        }
    }

    // ---------- helpers (business rules) ----------
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private static String normalizeCategoryForMealType(String mealType) {
        if (mealType == null) return null;
        String m = mealType.trim().toLowerCase(Locale.ROOT);
        if (m.equals("breakfast")) return "Breakfast";
        return null; // Lunch/Dinner not native categories
    }

    private Set<String> normalizeAreasForCuisine(String cuisine) {
        if (cuisine == null || cuisine.isBlank() || cuisine.equalsIgnoreCase("Any")) return Set.of();
        String c = cuisine.trim().toLowerCase(Locale.ROOT);
        Map<String, String> direct = Map.ofEntries(
                Map.entry("american","American"), Map.entry("british","British"), Map.entry("canadian","Canadian"),
                Map.entry("chinese","Chinese"), Map.entry("dutch","Dutch"), Map.entry("egyptian","Egyptian"),
                Map.entry("french","French"), Map.entry("greek","Greek"), Map.entry("indian","Indian"),
                Map.entry("irish","Irish"), Map.entry("italian","Italian"), Map.entry("jamaican","Jamaican"),
                Map.entry("japanese","Japanese"), Map.entry("kenyan","Kenyan"), Map.entry("malaysian","Malaysian"),
                Map.entry("mexican","Mexican"), Map.entry("moroccan","Moroccan"), Map.entry("polish","Polish"),
                Map.entry("portuguese","Portuguese"), Map.entry("russian","Russian"), Map.entry("spanish","Spanish"),
                Map.entry("thai","Thai"), Map.entry("tunisian","Tunisian"), Map.entry("turkish","Turkish"),
                Map.entry("vietnamese","Vietnamese")
        );
        if (direct.containsKey(c)) return Set.of(direct.get(c));
        if (c.equals("asian"))     return new LinkedHashSet<>(Arrays.asList("Chinese","Japanese","Thai","Vietnamese","Indian","Malaysian"));
        if (c.equals("european"))  return new LinkedHashSet<>(Arrays.asList("British","French","Greek","Irish","Italian","Polish","Portuguese","Russian","Spanish","Dutch"));
        return Set.of();
    }

    private static List<String> tokenizeIngredients(String qValue) {
        if (qValue == null) return List.of();
        String[] rough = qValue.toLowerCase(Locale.ROOT).split("[,\\s]+");
        List<String> toks = new ArrayList<>();
        for (String r : rough) {
            String t = r.trim();
            if (t.length() >= 2 && t.chars().allMatch(ch -> Character.isLetter(ch) || ch == '_')) {
                toks.add(t.replace(' ', '_'));
            }
        }
        return toks;
    }

    private Set<String> intersectByIngredients(String qValue) throws Exception {
        List<String> tokens = tokenizeIngredients(qValue);
        if (tokens.isEmpty()) return Set.of();
        Set<String> result = null;
        for (String t : tokens) {
            Set<String> ids = gateway.filterByIngredient(t);
            if (result == null) result = new LinkedHashSet<>(ids);
            else result.retainAll(ids);
            if (result.isEmpty()) break;
        }
        return result == null ? Set.of() : result;
    }

    private static Set<String> extractIds(List<RecipeDB> recipes) {
        // We don't have ids on RecipeDB, so if searchByName returns full recipes,
        // we can't extract ids here; keep this empty to avoid confusion.
        return Set.of();
    }

    private static int scoreMeal(RecipeDB meal, List<String> wanted, String category, Set<String> areas) {
        int score = 0;

        // ingredient partial matches
        Set<String> lowerIngs = meal.getIngredients().stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        for (String w : wanted) {
            String ws = w.replace('_',' ');
            for (String ing : lowerIngs) {
                if (ing.contains(ws)) { score++; break; }
            }
        }
        // area/category bonuses
        if (meal.getArea() != null && !meal.getArea().isBlank() && areas.contains(meal.getArea())) score += 2;
        if (category != null && meal.getCategory() != null &&
                meal.getCategory().equalsIgnoreCase(category)) score += 2;
        return score;
    }

    private static List<RecipeDB> applyDietFilter(List<RecipeDB> all, Diet desired) {
        if (desired == null || desired == Diet.NONE) return all;
        List<RecipeDB> out = new ArrayList<>();
        for (RecipeDB r : all) {
            switch (desired) {
                case VEGETARIAN:
                    if (r.getDiet() == Diet.VEGETARIAN || r.getDiet() == Diet.VEGAN) out.add(r);
                    break;
                case VEGAN:
                    if (r.getDiet() == Diet.VEGAN) out.add(r);
                    break;
                case NON_VEGETARIAN:
                    if (r.getDiet() == Diet.NON_VEGETARIAN) out.add(r);
                    break;
                default:
                    out.add(r);
            }
        }
        return out;
    }
}

