//package use_case.recipe;
//
//import entity.Diet;
//import entity.Recipe;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//public class RecipeService {
//
//    // Parse recipes.txt written by Demo3
//    public static List<Recipe> parseRecipesTxt(List<String> lines) {
//        List<Recipe> out = new ArrayList<>();
//        Recipe cur = null;
//        boolean inInstr = false;
//        for (String raw : lines) {
//            String line = raw.strip();
//            if (line.isEmpty()) {
//                if (cur != null) {
//                    cur.setDiet(inferDiet(cur.getIngredients()));
//                    out.add(cur);
//                    cur = null;
//                    inInstr = false;
//                }
//                continue;
//            }
//            if (cur == null) {
//                cur = new Recipe();
//                cur.setName(line);
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
//                cur.getIngredients().add(line);
//            } else {
//                cur.getInstructions().add(line.replaceFirst("^\\d+\\.?\\s*", "").trim());
//            }
//        }
//        if (cur != null) {
//            cur.setDiet(inferDiet(cur.getIngredients()));
//            out.add(cur);
//        }
//        return out;
//    }
//
//    public static List<Recipe> filterByDiet(List<Recipe> all, Diet desired) {
//        if (desired == Diet.NONE) return all;
//
//        List<Recipe> out = new ArrayList<>();
//        for (Recipe r : all) {
//            switch (desired) {
//                case VEGETARIAN:
//                    if (r.getDiet() == Diet.VEGETARIAN || r.getDiet() == Diet.VEGAN) out.add(r);
//                    break;
//                case VEGAN:
//                    if (r.getDiet() == Diet.VEGAN) out.add(r);
//                    break;
//                case NON_VEGETARIAN:
//                    if (r.getDiet() == Diet.NON_VEGETARIAN) out.add(r);
//                    break;
//                default:
//                    out.add(r);
//            }
//        }
//        return out;
//    }
//
//    // Heuristic dietary inference from ingredients
//    public static Diet inferDiet(List<String> ingredients) {
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
//                "egg","eggs","milk","butter","ghee","cheese","yoghurt","yogurt","cream",
//                "honey","gelatin","gelatine"
//        };
//        boolean hasAnimalProduct = false;
//        for (String t : animalProducts) if (blob.contains(t)) { hasAnimalProduct = true; break; }
//
//        return hasAnimalProduct ? Diet.VEGETARIAN : Diet.VEGAN;
//    }
//
//    // Fetch random recipe from TheMealDB API
//    public static Recipe fetchRandomRecipe() throws Exception {
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest req = HttpRequest.newBuilder(
//                URI.create("https://www.themealdb.com/api/json/v1/1/random.php")).GET().build();
//        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
//
//        ObjectMapper om = new ObjectMapper();
//        JsonNode root = om.readTree(resp.body());
//        JsonNode m = root.path("meals").get(0);
//
//        Recipe r = new Recipe();
//        r.setName(m.path("strMeal").asText());
//        r.setArea(m.path("strArea").asText());
//
//        for (int i = 1; i <= 20; i++) {
//            String ing = m.path("strIngredient" + i).asText("").trim();
//            String mea = m.path("strMeasure" + i).asText("").trim();
//            if (ing.isEmpty()) break;
//            String line = (mea.isEmpty() ? "" : mea + " ") + ing;
//            r.getIngredients().add(line.trim());
//        }
//
//        String instr = m.path("strInstructions").asText("");
//        r.setInstructions(splitSteps(instr));
//        r.setDiet(inferDiet(r.getIngredients()));
//        return r;
//    }
//
//    public static List<String> splitSteps(String instructions) {
//        if (instructions == null) return List.of();
//        String norm = instructions.replace("\r", "\n").trim();
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
//}
