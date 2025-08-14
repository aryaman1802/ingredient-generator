//package data_access;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import entity.Diet;
//import entity.Recipe;
//import use_case.gateway.RecipeGateway;
//
//import java.io.IOException;
//import java.lang.reflect.Method;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//public class MealDbApiGateway implements RecipeGateway {
//
//    private final HttpClient client = HttpClient.newHttpClient();
//    private final ObjectMapper om = new ObjectMapper();
//
//    @Override
//    public List<String> listCuisines() throws Exception {
//        List<String> out = new ArrayList<>();
//        out.add("Any");
//        try {
//            HttpRequest req = HttpRequest.newBuilder(
//                    URI.create("https://www.themealdb.com/api/json/v1/1/list.php?a=list")).GET().build();
//            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
//            JsonNode arr = om.readTree(resp.body()).path("meals");
//            if (arr.isArray()) {
//                for (JsonNode a : arr) {
//                    String area = a.path("strArea").asText();
//                    if (!area.isBlank()) out.add(area);
//                }
//                return out;
//            }
//        } catch (Exception ignored) {
//            // fall through to fallback
//        }
//        // Fallback list (kept small)
//        out.addAll(List.of("American","British","Canadian","Chinese","French","Greek",
//                "Indian","Italian","Japanese","Mexican","Moroccan","Spanish","Thai","Turkish","Vietnamese"));
//        return out;
//    }
//
//    @Override
//    public void writePreferences(String query, String mealType, String cuisine) throws IOException {
//        Path prefs = Paths.get("Preferences.txt");
//        List<String> lines = List.of(query, mealType, cuisine); // exactly 3 lines as per your Demo3
//        Files.write(prefs, lines, StandardCharsets.UTF_8,
//                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//    }
//
//    @Override
//    public void runDemo3() throws Exception {
//        // Try default package, then "view"
//        Class<?> clazz;
//        try {
//            clazz = Class.forName("Demo3");
//        } catch (ClassNotFoundException e) {
//            clazz = Class.forName("view.Demo3");
//        }
//        Method main = clazz.getMethod("main", String[].class);
//        main.invoke(null, (Object) new String[]{});
//    }
//
//    @Override
//    public List<Recipe> readAndParseRecipes(Path path) throws IOException {
//        if (!Files.exists(path)) {
//            throw new IOException("recipes.txt not found after Demo3 run.");
//        }
//        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
//        return parseRecipesTxt(lines);
//    }
//
//    @Override
//    public Recipe fetchRandomRecipe() throws Exception {
//        HttpRequest req = HttpRequest.newBuilder(
//                URI.create("https://www.themealdb.com/api/json/v1/1/random.php")).GET().build();
//        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
//        JsonNode m = om.readTree(resp.body()).path("meals").get(0);
//
//        Recipe r = new Recipe();
//        r.name = m.path("strMeal").asText();
//        r.area = m.path("strArea").asText();
//
//        for (int i = 1; i <= 20; i++) {
//            String ing = m.path("strIngredient" + i).asText("").trim();
//            String mea = m.path("strMeasure" + i).asText("").trim();
//            if (ing.isEmpty()) break;
//            String line = (mea.isEmpty() ? "" : mea + " ") + ing;
//            r.ingredients.add(line.trim());
//        }
//
//        r.instructions = splitSteps(m.path("strInstructions").asText(""));
//        r.diet = inferDiet(r.ingredients);
//        return r;
//    }
//
//    // ---------- Local helpers (not exposed in the gateway interface) ----------
//
//    private List<Recipe> parseRecipesTxt(List<String> lines) {
//        List<Recipe> out = new ArrayList<>();
//        Recipe cur = null;
//        boolean inInstr = false;
//
//        for (String raw : lines) {
//            String line = raw.strip();
//            if (line.isEmpty()) {
//                if (cur != null) {
//                    cur.diet = inferDiet(cur.ingredients);
//                    out.add(cur);
//                    cur = null;
//                    inInstr = false;
//                }
//                continue;
//            }
//            if (cur == null) {
//                cur = new Recipe();
//                cur.name = line;
//                inInstr = false;
//                continue;
//            }
//            if (line.equalsIgnoreCase("Instructions:")) {
//                inInstr = true;
//                continue;
//            }
//            if (!inInstr) {
//                if (line.startsWith("-")) line = line.substring(1).trim();
//                if (line.startsWith("•")) line = line.substring(1).trim();
//                cur.ingredients.add(line);
//            } else {
//                cur.instructions.add(line.replaceFirst("^\\d+\\.?\\s*", "").trim());
//            }
//        }
//        if (cur != null) {
//            cur.diet = inferDiet(cur.ingredients);
//            out.add(cur);
//        }
//        return out;
//    }
//
//    private List<String> splitSteps(String instructions) {
//        if (instructions == null) return List.of();
//        String norm = instructions.replace("\r", "\n").trim();
//
//        String[] byPara = norm.split("\\n\\s*\\n+");
//        List<String> steps = new ArrayList<>();
//        if (byPara.length > 1) {
//            for (String p : byPara) {
//                String t = p.trim();
//                if (!t.isEmpty()) steps.add(t);
//            }
//            return steps;
//        }
//        String[] byNl = norm.split("\\n+");
//        if (byNl.length > 1) {
//            for (String p : byNl) {
//                String t = p.trim();
//                if (!t.isEmpty()) steps.add(t);
//            }
//            return steps;
//        }
//        String[] bySent = norm.split("(?<=\\.)\\s+");
//        for (String s : bySent) {
//            String t = s.trim();
//            if (!t.isEmpty()) steps.add(t);
//        }
//        if (steps.isEmpty() && !norm.isEmpty()) steps.add(norm);
//        return steps;
//    }
//
//    private Diet inferDiet(List<String> ingredients) {
//        String blob = String.join(" | ", ingredients).toLowerCase(Locale.ROOT);
//
//        String[] nonVeg = {
//                "chicken","beef","pork","lamb","mutton","veal","turkey","duck","bacon","ham",
//                "prosciutto","chorizo","sausage","meat","steak","mince","anchovy","fish","salmon",
//                "tuna","cod","haddock","sardine","prawn","shrimp","crab","lobster","clam","mussel",
//                "oyster","octopus","squid"
//        };
//        for (String t : nonVeg) if (blob.contains(t)) return Diet.NON_VEGETARIAN;
//
//        String[] animalProducts = {
//                "egg","eggs","milk","butter","ghee","cheese","yoghurt","yogurt","cream","honey","gelatin","gelatine"
//        };
//        for (String t : animalProducts) if (blob.contains(t)) return Diet.VEGETARIAN;
//
//        return Diet.VEGAN;
//    }
//}




package data_access;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Diet;
import entity.Recipe;
import use_case.gateway.RecipeGateway;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MealDbApiGateway implements RecipeGateway {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper om = new ObjectMapper();
    private static final String BASE = "https://www.themealdb.com/api/json/v1/1/";

    // ---------------- existing methods (unchanged) ----------------
    @Override public List<String> listCuisines() throws Exception { /* ...unchanged from earlier... */
        List<String> out = new ArrayList<>();
        out.add("Any");
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + "list.php?a=list")).GET().build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode arr = om.readTree(resp.body()).path("meals");
            if (arr.isArray()) {
                for (JsonNode a : arr) {
                    String area = a.path("strArea").asText();
                    if (!area.isBlank()) out.add(area);
                }
                return out;
            }
        } catch (Exception ignored) {}
        out.addAll(List.of("American","British","Canadian","Chinese","French","Greek",
                "Indian","Italian","Japanese","Mexican","Moroccan","Spanish","Thai","Turkish","Vietnamese"));
        return out;
    }

    @Override public void writePreferences(String query, String mealType, String cuisine) throws IOException {
        Path prefs = Paths.get("Preferences.txt");
        Files.write(prefs, List.of(query, mealType, cuisine), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override public void runDemo3() throws Exception {
        Class<?> clazz;
        try { clazz = Class.forName("Demo3"); }
        catch (ClassNotFoundException e) { clazz = Class.forName("view.Demo3"); }
        Method main = clazz.getMethod("main", String[].class);
        main.invoke(null, (Object) new String[]{});
    }

    @Override public List<Recipe> readAndParseRecipes(Path path) throws IOException {
        if (!Files.exists(path)) throw new IOException("recipes.txt not found after Demo3 run.");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        return parseRecipesTxt(lines);
    }

    @Override public Recipe fetchRandomRecipe() throws Exception {
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "random.php")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        JsonNode m = om.readTree(resp.body()).path("meals").get(0);
        return toRecipeFull(m);
    }

    // ---------------- NEW: endpoints Demo3 needs ----------------
    @Override public List<Recipe> searchByName(String query) throws Exception {
        String q = query == null ? "" : query.trim();
        if (q.isEmpty()) return List.of();
        JsonNode arr = getJson(BASE + "search.php?s=" + uri(q)).path("meals");
        if (arr.isMissingNode() || arr.isNull()) return List.of();
        List<Recipe> out = new ArrayList<>();
        for (JsonNode m : arr) out.add(toRecipeFull(m));
        return out;
    }

    @Override public List<Recipe> searchByFirstLetter(String letter) throws Exception {
        JsonNode arr = getJson(BASE + "search.php?f=" + uri(letter)).path("meals");
        if (arr.isMissingNode() || arr.isNull()) return List.of();
        List<Recipe> out = new ArrayList<>();
        for (JsonNode m : arr) out.add(toRecipeFull(m));
        return out;
    }

    @Override public Set<String> filterByIngredient(String ingredient) throws Exception {
        if (ingredient == null || ingredient.trim().isEmpty()) return Set.of();
        JsonNode arr = getJson(BASE + "filter.php?i=" + uri(ingredient)).path("meals");
        if (arr.isMissingNode() || arr.isNull()) return Set.of();
        Set<String> ids = new LinkedHashSet<>();
        for (JsonNode m : arr) ids.add(m.path("idMeal").asText());
        return ids;
    }

    @Override public Set<String> filterByArea(String area) throws Exception {
        JsonNode arr = getJson(BASE + "filter.php?a=" + uri(area)).path("meals");
        if (arr.isMissingNode() || arr.isNull()) return Set.of();
        Set<String> ids = new LinkedHashSet<>();
        for (JsonNode m : arr) ids.add(m.path("idMeal").asText());
        return ids;
    }

    @Override public Set<String> filterByCategory(String category) throws Exception {
        JsonNode arr = getJson(BASE + "filter.php?c=" + uri(category)).path("meals");
        if (arr.isMissingNode() || arr.isNull()) return Set.of();
        Set<String> ids = new LinkedHashSet<>();
        for (JsonNode m : arr) ids.add(m.path("idMeal").asText());
        return ids;
    }

    @Override public Recipe lookupById(String id) throws Exception {
        JsonNode arr = getJson(BASE + "lookup.php?i=" + uri(id)).path("meals");
        if (arr.isMissingNode() || arr.isNull() || arr.size() == 0) return null;
        return toRecipeFull(arr.get(0));
    }

    // ---------------- helpers ----------------
    private JsonNode getJson(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return om.readTree(resp.body());
    }
    private static String uri(String s) { return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8); }

    private Recipe toRecipeFull(JsonNode m) {
        Recipe r = new Recipe();
        r.name = m.path("strMeal").asText();
        r.area = text(m, "strArea");
        r.category = text(m, "strCategory");

        for (int i = 1; i <= 20; i++) {
            String ing = text(m, "strIngredient" + i).trim();
            String mea = text(m, "strMeasure" + i).trim();
            if (ing.isBlank()) break;
            r.rawIngredients.add(ing.toLowerCase(Locale.ROOT));
            r.ingredients.add((mea.isBlank() ? "" : (mea + " ")) + ing);
        }

        r.instructions = splitSteps(text(m, "strInstructions"));
        r.diet = inferDiet(r.ingredients);
        return r;
    }

    private static String text(JsonNode n, String field) {
        JsonNode v = n.get(field);
        return (v == null || v.isNull()) ? "" : v.asText("");
    }

    private static List<String> splitSteps(String instructions) {
        if (instructions == null) return List.of();
        String norm = instructions.replace("\r", "\n").trim();
        List<String> byNl = Arrays.stream(norm.split("\\n+"))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        if (byNl.size() > 1) return byNl;
        String[] parts = norm.split("(?<=\\.)\\s+");
        List<String> out = new ArrayList<>();
        for (String p : parts) { String t = p.trim(); if (!t.isEmpty()) out.add(t); }
        return out.isEmpty() ? (norm.isEmpty() ? List.of() : List.of(norm)) : out;
    }

    private static Diet inferDiet(List<String> ingredientLines) {
        String blob = String.join(" | ", ingredientLines).toLowerCase(Locale.ROOT);
        String[] nonVeg = {"chicken","beef","pork","lamb","mutton","veal","turkey","duck","bacon","ham",
                "prosciutto","chorizo","sausage","meat","steak","mince","anchovy","fish","salmon","tuna","cod",
                "haddock","sardine","prawn","shrimp","crab","lobster","clam","mussel","oyster","octopus","squid"};
        for (String t : nonVeg) if (blob.contains(t)) return Diet.NON_VEGETARIAN;
        String[] animal = {"egg","eggs","milk","butter","ghee","cheese","yoghurt","yogurt","cream","honey","gelatin","gelatine"};
        for (String t : animal) if (blob.contains(t)) return Diet.VEGETARIAN;
        return Diet.VEGAN;
    }

    // Kept from earlier so MealDBSwingApp parsing keeps working if needed elsewhere
    private List<Recipe> parseRecipesTxt(List<String> lines) {
        List<Recipe> out = new ArrayList<>();
        Recipe cur = null; boolean inInstr = false;
        for (String raw : lines) {
            String line = raw.strip();
            if (line.isEmpty()) {
                if (cur != null) { cur.diet = inferDiet(cur.ingredients); out.add(cur); cur = null; inInstr = false; }
                continue;
            }
            if (cur == null) { cur = new Recipe(); cur.name = line; inInstr = false; continue; }
            if (line.equalsIgnoreCase("Instructions:")) { inInstr = true; continue; }
            if (!inInstr) {
                if (line.startsWith("-")) line = line.substring(1).trim();
                if (line.startsWith("•")) line = line.substring(1).trim();
                cur.ingredients.add(line);
            } else {
                cur.instructions.add(line.replaceFirst("^\\d+\\.?\\s*", "").trim());
            }
        }
        if (cur != null) { cur.diet = inferDiet(cur.ingredients); out.add(cur); }
        return out;
    }
}
