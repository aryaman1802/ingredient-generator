package use_case.demo3;

import entity.Recipe;
import use_case.gateway.RecipeGateway;

import java.util.*;

public class BuildTopRecipesFromPreferencesInteractor implements BuildTopRecipesFromPreferencesUseCase {

    private final RecipeGateway gw;

    public BuildTopRecipesFromPreferencesInteractor(RecipeGateway gw) {
        this.gw = gw;
    }

    @Override
    public List<Recipe> execute(String qValue, String mealTypeValue, String cuisineTypeValue) throws Exception {
        String categoryFilter = normalizeCategoryForMealType(mealTypeValue);      // "Breakfast" or null
        Set<String> areaFilters = normalizeAreasForCuisine(cuisineTypeValue);     // possibly empty

        // Name search (full details) – keep their ids and also keep the list for fallback
        List<Recipe> searchMeals = gw.searchByName(qValue);
        Set<String> candidateIds = new LinkedHashSet<>();
        for (Recipe r : searchMeals) candidateIds.add(extractId(r)); // id lives in lookup-only; useName fallback below

        // Ingredients intersection
        Set<String> ingredientIds = intersectByIngredients(qValue);
        if (!ingredientIds.isEmpty()) {
            if (candidateIds.isEmpty()) candidateIds.addAll(ingredientIds);
            else candidateIds.retainAll(ingredientIds);
        }

        // Area filter
        if (!areaFilters.isEmpty()) {
            Set<String> byArea = new LinkedHashSet<>();
            for (String area : areaFilters) byArea.addAll(gw.filterByArea(area));
            if (!byArea.isEmpty()) {
                if (candidateIds.isEmpty()) candidateIds.addAll(byArea);
                else candidateIds.retainAll(byArea);
            }
        }

        // Category filter (Breakfast only)
        if (categoryFilter != null) {
            Set<String> byCat = gw.filterByCategory(categoryFilter);
            if (!byCat.isEmpty()) {
                if (candidateIds.isEmpty()) candidateIds.addAll(byCat);
                else candidateIds.retainAll(byCat);
            }
        }

        // Fallbacks
        if (candidateIds.isEmpty() && !searchMeals.isEmpty()) {
            // use ids from name search results
            for (Recipe r : searchMeals) {
                String id = extractId(r); if (id != null) candidateIds.add(id);
            }
        }
        if (candidateIds.isEmpty() && categoryFilter != null) {
            candidateIds.addAll(gw.filterByCategory(categoryFilter));
        }
        if (candidateIds.isEmpty() && !areaFilters.isEmpty()) {
            for (String area : areaFilters) candidateIds.addAll(gw.filterByArea(area));
        }

        if (candidateIds.isEmpty() && qValue != null && !qValue.isBlank()) {
            char firstChar = Character.toLowerCase(qValue.charAt(0));
            if (Character.isLetter(firstChar)) {
                for (Recipe r : gw.searchByFirstLetter(String.valueOf(firstChar))) {
                    String id = extractId(r); if (id != null) candidateIds.add(id);
                }
            }
        }

        if (candidateIds.isEmpty()) return List.of();

        // hydrate details
        List<Recipe> detailedMeals = new ArrayList<>();
        for (String id : candidateIds) {
            Recipe full = gw.lookupById(id);
            if (full != null) detailedMeals.add(full);
        }

        // Rank
        List<String> wanted = tokenizeIngredients(qValue);
        detailedMeals.sort((a, b) -> Integer.compare(
                scoreMeal(b, wanted, categoryFilter, areaFilters),
                scoreMeal(a, wanted, categoryFilter, areaFilters)));

        return detailedMeals.subList(0, Math.min(3, detailedMeals.size()));
    }

    // ---------------- scoring & helpers (pure) ----------------
    private static int scoreMeal(Recipe meal, List<String> wanted, String category, Set<String> areas) {
        int score = 0;
        // ingredient matches (normalize underscore->space)
        for (String w : wanted) {
            String ws = w.replace('_', ' ');
            boolean matched = false;
            for (String ing : meal.rawIngredients) {
                if (ing.contains(ws)) { matched = true; break; }
            }
            if (matched) score++;
        }
        if (meal.area != null && !meal.area.isBlank() && areas.contains(meal.area)) score += 2;
        if (meal.category != null && category != null &&
                meal.category.equalsIgnoreCase(category)) score += 2;
        return score;
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
            Set<String> ids = gw.filterByIngredient(t);
            if (result == null) result = new LinkedHashSet<>(ids);
            else result.retainAll(ids);
            if (result.isEmpty()) break;
        }
        return result == null ? Set.of() : result;
    }

    private static String normalizeCategoryForMealType(String mealType) {
        if (mealType == null) return null;
        String m = mealType.trim().toLowerCase(Locale.ROOT);
        if (m.equals("breakfast")) return "Breakfast";
        return null; // Lunch/Dinner not supported as categories by TheMealDB
    }

    private static Set<String> normalizeAreasForCuisine(String cuisine) {
        if (cuisine == null || cuisine.isBlank()) return Set.of();
        String c = cuisine.trim().toLowerCase(Locale.ROOT);
        Map<String,String> direct = Map.ofEntries(
                Map.entry("american","American"), Map.entry("british","British"),
                Map.entry("canadian","Canadian"), Map.entry("chinese","Chinese"),
                Map.entry("dutch","Dutch"), Map.entry("egyptian","Egyptian"),
                Map.entry("french","French"), Map.entry("greek","Greek"),
                Map.entry("indian","Indian"), Map.entry("irish","Irish"),
                Map.entry("italian","Italian"), Map.entry("jamaican","Jamaican"),
                Map.entry("japanese","Japanese"), Map.entry("kenyan","Kenyan"),
                Map.entry("malaysian","Malaysian"), Map.entry("mexican","Mexican"),
                Map.entry("moroccan","Moroccan"), Map.entry("polish","Polish"),
                Map.entry("portuguese","Portuguese"), Map.entry("russian","Russian"),
                Map.entry("spanish","Spanish"), Map.entry("thai","Thai"),
                Map.entry("tunisian","Tunisian"), Map.entry("turkish","Turkish"),
                Map.entry("vietnamese","Vietnamese")
        );
        if (direct.containsKey(c)) return Set.of(direct.get(c));
        if (c.equals("asian")) return new LinkedHashSet<>(Arrays.asList("Chinese","Japanese","Thai","Vietnamese","Indian","Malaysian"));
        if (c.equals("european")) return new LinkedHashSet<>(Arrays.asList("British","French","Greek","Irish","Italian","Polish","Portuguese","Russian","Spanish","Dutch"));
        return Set.of();
    }

    // When using name/letter search we don’t have ids easily; we’ll just null this and rely on lookups/fallbacks.
    private static String extractId(Recipe r) { return null; }
}
