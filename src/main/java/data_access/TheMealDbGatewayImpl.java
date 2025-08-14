package data_access;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.DietRules;
import entity.Recipe;
import entity.RecipeDB;
import use_case.gateway.MealDbGateway;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TheMealDbGatewayImpl implements MealDbGateway {
    private static final String BASE = "https://www.themealdb.com/api/json/v1/1/";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public List<RecipeDB> searchByName(String query) throws Exception {
        if (query == null || query.isBlank()) return List.of();
        String url = BASE + "search.php?s=" + enc(query);
        JsonNode arr = root(url).path("meals");
        if (arr == null || arr.isNull()) return List.of();
        List<RecipeDB> out = new ArrayList<>();
        for (JsonNode m : arr) out.add(toRecipe(m));
        return out;
    }

    @Override
    public List<RecipeDB> searchByFirstLetter(String letter) throws Exception {
        String url = BASE + "search.php?f=" + enc(letter);
        JsonNode arr = root(url).path("meals");
        if (arr == null || arr.isNull()) return List.of();
        List<RecipeDB> out = new ArrayList<>();
        for (JsonNode m : arr) out.add(toRecipe(m));
        return out;
    }

    @Override
    public Set<String> filterByIngredient(String ingredient) throws Exception {
        if (ingredient == null || ingredient.isBlank()) return Set.of();
        String url = BASE + "filter.php?i=" + enc(ingredient.trim().toLowerCase());
        JsonNode arr = root(url).path("meals");
        if (arr == null || arr.isNull()) return Set.of();
        Set<String> ids = new LinkedHashSet<>();
        for (JsonNode m : arr) ids.add(m.path("idMeal").asText());
        return ids;
    }

    @Override
    public Set<String> filterByArea(String area) throws Exception {
        String url = BASE + "filter.php?a=" + enc(area);
        JsonNode arr = root(url).path("meals");
        if (arr == null || arr.isNull()) return Set.of();
        Set<String> ids = new LinkedHashSet<>();
        for (JsonNode m : arr) ids.add(m.path("idMeal").asText());
        return ids;
    }

    @Override
    public Set<String> filterByCategory(String category) throws Exception {
        String url = BASE + "filter.php?c=" + enc(category);
        JsonNode arr = root(url).path("meals");
        if (arr == null || arr.isNull()) return Set.of();
        Set<String> ids = new LinkedHashSet<>();
        for (JsonNode m : arr) ids.add(m.path("idMeal").asText());
        return ids;
    }

    @Override
    public Optional<RecipeDB> lookupById(String id) throws Exception {
        String url = BASE + "lookup.php?i=" + enc(id);
        JsonNode arr = root(url).path("meals");
        if (arr == null || arr.isNull() || arr.size() == 0) return Optional.empty();
        return Optional.of(toRecipe(arr.get(0)));
    }

    @Override
    public RecipeDB random() throws Exception {
        String url = BASE + "random.php";
        JsonNode arr = root(url).path("meals");
        if (arr == null || arr.isNull() || arr.size() == 0) {
            throw new RuntimeException("No random meal returned");
        }
        return toRecipe(arr.get(0));
    }

    @Override
    public List<String> listAreas() throws Exception {
        String url = BASE + "list.php?a=list";
        JsonNode arr = root(url).path("meals");
        List<String> out = new ArrayList<>();
        if (arr != null && arr.isArray()) {
            out.add("Any");
            for (JsonNode a : arr) {
                String area = a.path("strArea").asText();
                if (!area.isBlank()) out.add(area);
            }
        }
        return out;
    }

    // ---------- helpers ----------
    private JsonNode root(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return om.readTree(resp.body());
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private RecipeDB toRecipe(JsonNode m) {
        RecipeDB r = new RecipeDB();
        r.setName(m.path("strMeal").asText());
        r.setArea(m.path("strArea").asText(""));
        r.setCategory(m.path("strCategory").asText(""));

        for (int i = 1; i <= 20; i++) {
            String ing = text(m, "strIngredient" + i);
            String mea = text(m, "strMeasure" + i);
            if (ing.isBlank()) break;
            String line = (mea.isBlank() ? "" : (mea + " ")) + ing;
            r.addIngredient(line.trim());
        }

        String instructions = m.path("strInstructions").asText("");
        for (String step : splitSteps(instructions)) r.addInstruction(step);

        r.setDiet(DietRules.inferDiet(r.getIngredients()));
        return r;
    }

    private static String text(JsonNode n, String field) {
        JsonNode v = n.get(field);
        return (v == null || v.isNull()) ? "" : v.asText("");
    }

    private static List<String> splitSteps(String instructions) {
        if (instructions == null) return List.of();
        String norm = instructions.replace("\r", "\n").trim();
        String[] byPara = norm.split("\\n\\s*\\n+");
        List<String> steps = new ArrayList<>();
        if (byPara.length > 1) {
            for (String p : byPara) { String t = p.trim(); if (!t.isEmpty()) steps.add(t); }
            return steps;
        }
        String[] byNl = norm.split("\\n+");
        if (byNl.length > 1) {
            for (String p : byNl) { String t = p.trim(); if (!t.isEmpty()) steps.add(t); }
            return steps;
        }
        String[] bySent = norm.split("(?<=\\.)\\s+");
        for (String s : bySent) { String t = s.trim(); if (!t.isEmpty()) steps.add(t); }
        if (steps.isEmpty() && !norm.isEmpty()) steps.add(norm);
        return steps;
    }
}

