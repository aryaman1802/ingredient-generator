package view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Demo3 {

    // TheMealDB free dev key "1" (sufficient for this use-case)
    private static final String BASE = "https://www.themealdb.com/api/json/v1/1/";

    public static void main(String[] args) throws Exception {
        // 1) Read Preferences.txt from project directory (same as your original program)
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path filePath = Paths.get(currentPath.toString(), "Preferences.txt");
        String inputFilePath = filePath.toString();

        List<String> lines = Files.readAllLines(Paths.get(inputFilePath), StandardCharsets.UTF_8);
        if (lines.size() < 3) {
            System.err.println("Preferences.txt must have 3 lines: <query>, <mealType>, <cuisineType>");
            return;
        }

        String qValue           = lines.get(0).trim();  // e.g. "Milk,eggs,chicken" OR "Chicken Alfredo"
        String mealTypeValue    = lines.get(1).trim();  // e.g. "Breakfast" | "Lunch" | "Dinner"
        String cuisineTypeValue = lines.get(2).trim();  // e.g. "Indian" | "Canadian" | "Chinese"

        System.out.println("Input:");
        System.out.println("  Query: " + qValue);
        System.out.println("  Meal type: " + mealTypeValue);
        System.out.println("  Cuisine: " + cuisineTypeValue);

        ObjectMapper mapper = new ObjectMapper();

        // 2) Normalize filters
        // Meal type -> category (Breakfast supported; Lunch/Dinner not native categories in TheMealDB)
        String categoryFilter = normalizeCategoryForMealType(mealTypeValue); // "Breakfast" or null

        // Cuisine -> Area(s)
        Set<String> areaFilters = normalizeAreasForCuisine(cuisineTypeValue); // could be multiple for "Asian"

        // 3) Collect candidate IDs via three strategies (free endpoints):
        //    A) Name search (dish name guess)
        //    B) Ingredient filters (intersect across multiple tokens to emulate multi-ingredient)
        //    C) Area/category filters (intersections)
        Set<String> candidateIds = new LinkedHashSet<>();

        // A) Direct dish name search (keeps full details now)
        List<JsonNode> searchMeals = searchByName(qValue, mapper);
        for (JsonNode m : searchMeals) {
            candidateIds.add(m.path("idMeal").asText());
        }

        // B) Ingredient intersection (split by commas or spaces)
        Set<String> ingredientIds = intersectByIngredients(qValue);
        if (!ingredientIds.isEmpty()) {
            if (candidateIds.isEmpty()) candidateIds.addAll(ingredientIds);
            else candidateIds.retainAll(ingredientIds);
        }

        // C1) Area filter — if user picked a cuisine
        if (!areaFilters.isEmpty()) {
            Set<String> byArea = new LinkedHashSet<>();
            for (String area : areaFilters) byArea.addAll(filterByArea(area));
            if (!byArea.isEmpty()) {
                if (candidateIds.isEmpty()) candidateIds.addAll(byArea);
                else candidateIds.retainAll(byArea);
            }
        }

        // C2) Category filter — only if "Breakfast" (TheMealDB supports 'Breakfast' explicitly)
        if (categoryFilter != null) {
            Set<String> byCat = filterByCategory(categoryFilter);
            if (!byCat.isEmpty()) {
                if (candidateIds.isEmpty()) candidateIds.addAll(byCat);
                else candidateIds.retainAll(byCat);
            }
        }

        // Fallbacks if we ended up empty:
        if (candidateIds.isEmpty() && !searchMeals.isEmpty()) {
            // Use name-search results if we had them
            for (JsonNode m : searchMeals) candidateIds.add(m.path("idMeal").asText());
        }
        if (candidateIds.isEmpty() && categoryFilter != null) {
            candidateIds.addAll(filterByCategory(categoryFilter));
        }
        if (candidateIds.isEmpty() && !areaFilters.isEmpty()) {
            for (String area : areaFilters) candidateIds.addAll(filterByArea(area));
        }

        // Still empty? Grab a few from the generic query via first-letter search (broad net)
        if (candidateIds.isEmpty() && !qValue.isEmpty()) {
            char firstChar = Character.toLowerCase(qValue.charAt(0));
            if (Character.isLetter(firstChar)) {
                for (JsonNode m : searchByFirstLetter(String.valueOf(firstChar), mapper)) {
                    candidateIds.add(m.path("idMeal").asText());
                }
            }
        }

        if (candidateIds.isEmpty()) {
            System.out.println("No meals found. Try simplifying your query.");
            return;
        }

        // 4) Fetch full details for each candidate and score/rank
        List<JsonNode> detailedMeals = new ArrayList<>();
        for (String id : candidateIds) {
            JsonNode meal = lookupById(id, mapper);
            if (meal != null) detailedMeals.add(meal);
        }

        // Rank: # of matched ingredients + light preference if category/area matched
        List<String> wantedIngredients = tokenizeIngredients(qValue);
        detailedMeals.sort((a, b) -> {
            int sa = scoreMeal(a, wantedIngredients, categoryFilter, areaFilters);
            int sb = scoreMeal(b, wantedIngredients, categoryFilter, areaFilters);
            return Integer.compare(sb, sa);
        });

        // 5) Write top 3 to recipes.txt in your original-ish format:
        //    Title
        //      - ingredient line
        //    Instructions:
        //    1. ...
        //    2. ...
        List<String> outLines = new ArrayList<>();
        int limit = Math.min(3, detailedMeals.size());
        for (int i = 0; i < limit; i++) {
            JsonNode meal = detailedMeals.get(i);
            String name = meal.path("strMeal").asText();
            outLines.add(name);

            // Ingredients (combine measure + ingredient)
            for (int k = 1; k <= 20; k++) {
                String ing = text(meal, "strIngredient" + k);
                String mea = text(meal, "strMeasure" + k);
                if (ing.isBlank()) break;
                String line = (mea.isBlank() ? "" : (mea.trim() + " ")) + ing.trim();
                outLines.add("  - " + line);
            }

            // Instructions (ordered steps)
            String instructions = meal.path("strInstructions").asText("");
            if (!instructions.isBlank()) {
                outLines.add("Instructions:");
                // Split into steps by newline or sentence-ish breaks
                List<String> steps = splitSteps(instructions);
                for (int s = 0; s < steps.size(); s++) {
                    outLines.add((s + 1) + ". " + steps.get(s));
                }
            }
            outLines.add(""); // blank line between meals
        }

        Path outFile = Paths.get("recipes.txt");
        Files.write(outFile, outLines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Saved " + outLines.size() + " lines to " + outFile.toAbsolutePath());
    }

    // ---------- Helpers ----------

    private static String text(JsonNode n, String field) {
        JsonNode v = n.get(field);
        return (v == null || v.isNull()) ? "" : v.asText("");
    }

    // Basic GET
    private static JsonNode getJson(String url, ObjectMapper mapper) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);
        conn.setRequestMethod("GET");
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            for (String line; (line = br.readLine()) != null; ) sb.append(line);
            return mapper.readTree(sb.toString());
        }
    }

    // Search by name -> full details (strInstructions present)
    private static List<JsonNode> searchByName(String query, ObjectMapper mapper) throws Exception {
        String q = query == null ? "" : query.trim();
        if (q.isEmpty()) return List.of();
        String url = BASE + "search.php?s=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
        JsonNode root = getJson(url, mapper);
        JsonNode arr = root.path("meals");
        if (arr.isMissingNode() || arr.isNull()) return List.of();
        List<JsonNode> out = new ArrayList<>();
        for (JsonNode m : arr) out.add(m);
        return out;
    }

    // Search by first letter -> full details list
    private static List<JsonNode> searchByFirstLetter(String letter, ObjectMapper mapper) throws Exception {
        String url = BASE + "search.php?f=" + URLEncoder.encode(letter, StandardCharsets.UTF_8);
        JsonNode root = getJson(url, mapper);
        JsonNode arr = root.path("meals");
        if (arr.isMissingNode() || arr.isNull()) return List.of();
        List<JsonNode> out = new ArrayList<>();
        for (JsonNode m : arr) out.add(m);
        return out;
    }

    // Filter by ONE ingredient -> returns ids (we'll intersect across multiple calls)
    private static Set<String> filterByIngredient(String ingredient) throws Exception {
        String q = ingredient.trim().toLowerCase();
        if (q.isEmpty()) return Set.of();
        String url = BASE + "filter.php?i=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
        JsonNode root = new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(getJson(url, new ObjectMapper())));
        root = getJson(url, new ObjectMapper());
        JsonNode arr = root.path("meals");
        if (arr.isMissingNode() || arr.isNull()) return Set.of();
        Set<String> ids = new LinkedHashSet<>();
        for (JsonNode m : arr) ids.add(m.path("idMeal").asText());
        return ids;
    }

    private static Set<String> filterByArea(String area) throws Exception {
        String url = BASE + "filter.php?a=" + URLEncoder.encode(area, StandardCharsets.UTF_8);
        JsonNode root = getJson(url, new ObjectMapper());
        JsonNode arr = root.path("meals");
        if (arr.isMissingNode() || arr.isNull()) return Set.of();
        Set<String> ids = new LinkedHashSet<>();
        for (JsonNode m : arr) ids.add(m.path("idMeal").asText());
        return ids;
    }

    private static Set<String> filterByCategory(String category) throws Exception {
        String url = BASE + "filter.php?c=" + URLEncoder.encode(category, StandardCharsets.UTF_8);
        JsonNode root = getJson(url, new ObjectMapper());
        JsonNode arr = root.path("meals");
        if (arr.isMissingNode() || arr.isNull()) return Set.of();
        Set<String> ids = new LinkedHashSet<>();
        for (JsonNode m : arr) ids.add(m.path("idMeal").asText());
        return ids;
    }

    private static JsonNode lookupById(String id, ObjectMapper mapper) throws Exception {
        String url = BASE + "lookup.php?i=" + URLEncoder.encode(id, StandardCharsets.UTF_8);
        JsonNode root = getJson(url, mapper);
        JsonNode arr = root.path("meals");
        if (arr.isMissingNode() || arr.isNull() || arr.size() == 0) return null;
        return arr.get(0);
    }

    // Intersect ingredient filters to emulate multi-ingredient search for free
    private static Set<String> intersectByIngredients(String qValue) throws Exception {
        List<String> tokens = tokenizeIngredients(qValue);
        if (tokens.isEmpty()) return Set.of();
        Set<String> result = null;
        for (String t : tokens) {
            Set<String> ids = filterByIngredient(t);
            if (result == null) result = new LinkedHashSet<>(ids);
            else result.retainAll(ids);
            if (result.isEmpty()) break;
        }
        return result == null ? Set.of() : result;
    }

    // Extract ingredient-ish tokens from user query (split on commas or spaces)
    private static List<String> tokenizeIngredients(String qValue) {
        if (qValue == null) return List.of();
        String q = qValue.toLowerCase(Locale.ROOT);
        String[] rough = q.split("[,\\s]+");
        List<String> toks = new ArrayList<>();
        for (String r : rough) {
            String t = r.trim();
            if (t.length() >= 2 && t.chars().allMatch(ch -> Character.isLetter(ch) || ch == '_'))
                toks.add(t.replace(' ', '_'));
        }
        return toks;
    }

    // Simple “Breakfast” mapping; Lunch/Dinner have no native category in TheMealDB
    private static String normalizeCategoryForMealType(String mealType) {
        if (mealType == null) return null;
        String m = mealType.trim().toLowerCase(Locale.ROOT);
        if (m.equals("breakfast")) return "Breakfast";
        // “Lunch”, “Dinner” -> no-op (keep broad)
        return null;
    }

    // Map cuisine to API “Area”. “Asian” expands to common Asian areas
    private static Set<String> normalizeAreasForCuisine(String cuisine) {
        if (cuisine == null || cuisine.isBlank()) return Set.of();
        String c = cuisine.trim().toLowerCase(Locale.ROOT);
        Map<String, String> direct = Map.ofEntries(
                Map.entry("american", "American"),
                Map.entry("british",  "British"),
                Map.entry("canadian", "Canadian"),
                Map.entry("chinese",  "Chinese"),
                Map.entry("dutch",    "Dutch"),
                Map.entry("egyptian", "Egyptian"),
                Map.entry("french",   "French"),
                Map.entry("greek",    "Greek"),
                Map.entry("indian",   "Indian"),
                Map.entry("irish",    "Irish"),
                Map.entry("italian",  "Italian"),
                Map.entry("jamaican", "Jamaican"),
                Map.entry("japanese", "Japanese"),
                Map.entry("kenyan",   "Kenyan"),
                Map.entry("malaysian","Malaysian"),
                Map.entry("mexican",  "Mexican"),
                Map.entry("moroccan", "Moroccan"),
                Map.entry("polish",   "Polish"),
                Map.entry("portuguese","Portuguese"),
                Map.entry("russian",  "Russian"),
                Map.entry("spanish",  "Spanish"),
                Map.entry("thai",     "Thai"),
                Map.entry("tunisian", "Tunisian"),
                Map.entry("turkish",  "Turkish"),
                Map.entry("vietnamese","Vietnamese")
        );

        if (direct.containsKey(c)) return Set.of(direct.get(c));
        if (c.equals("asian")) {
            return new LinkedHashSet<>(Arrays.asList("Chinese", "Japanese", "Thai", "Vietnamese", "Indian", "Malaysian"));
        }
        if (c.equals("european")) {
            return new LinkedHashSet<>(Arrays.asList("British", "French", "Greek", "Irish", "Italian", "Polish", "Portuguese", "Russian", "Spanish", "Dutch"));
        }
        return Set.of(); // unknown -> no area filter
    }

    // Score meal: number of matched ingredients + small bonuses for matching area/category
    private static int scoreMeal(JsonNode meal, List<String> wanted, String category, Set<String> areas) {
        int score = 0;

        // ingredient matches
        Set<String> ingredients = new HashSet<>();
        for (int k = 1; k <= 20; k++) {
            String ing = text(meal, "strIngredient" + k).toLowerCase(Locale.ROOT);
            if (ing.isBlank()) break;
            ingredients.add(ing);
        }
        for (String w : wanted) {
            // normalize underscore vs space
            String ws = w.replace('_', ' ');
            for (String ing : ingredients) {
                if (ing.contains(ws)) { score++; break; }
            }
        }

        // area/category bonus
        String area = text(meal, "strArea");
        if (!area.isBlank() && areas.contains(area)) score += 2;

        String cat = text(meal, "strCategory");
        if (!cat.isBlank() && category != null && cat.equalsIgnoreCase(category)) score += 2;

        return score;
    }

    // Convert instruction blob to numbered list
    private static List<String> splitSteps(String instructions) {
        // Prefer newline separation; fall back to sentence-ish split
        String norm = instructions.replace("\r", "\n").trim();
        List<String> byNl = Arrays.stream(norm.split("\\n+"))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        if (byNl.size() > 1) return byNl;

        // fallback: split on ". " but keep content tidy
        String[] parts = norm.split("(?<=\\.)\\s+");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out.isEmpty() ? List.of(norm) : out;
    }
}
