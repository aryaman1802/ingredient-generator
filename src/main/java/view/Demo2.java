package view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Demo2 {
    public static void main(String[] args) throws Exception {
        // 1. Specify the path to your input text file
//        String inputFilePath = "C:/Users/dell/Desktop/UofT/3rd year summer/CSC207/ingredient-generator/recipes.txt";
        String inputFilePath = "C:/Users/dell/Desktop/UofT/3rd year summer/CSC207/ingredient-generator/Preferences.txt";

        // 2. Read all lines from the file
        List<String> lines = Files.readAllLines(
                Paths.get(inputFilePath),
                StandardCharsets.UTF_8
        );
        if (lines.size() < 3) {
            System.err.println("Input file must contain at least 3 lines.");
            return;
        }

        // 3. Extract each parameter from the corresponding line
        String qValue           = lines.get(0).trim();  // e.g. "Milk,eggs,chicken"
        String mealTypeValue    = lines.get(1).trim();  // e.g. "Dinner"
        String cuisineTypeValue = lines.get(2).trim();  // e.g. "Indian"

        // 4. Build the params string dynamically
        String params = String.format(
                "?type=public" +
                        "&q=%s" +
                        "&app_id=49c7df87" +
                        "&app_key=1f9a5dc3d91f65bbf5eef79948d4e32b" +
                        "&diet=balanced" +
                        "&mealType=%s" +
                        "&cuisineType=%s" +
                        "&time=0-100",
                // it's a good idea to URL‑encode qValue if it may contain spaces/special chars:
                URLEncoder.encode(qValue, StandardCharsets.UTF_8),
                URLEncoder.encode(mealTypeValue, StandardCharsets.UTF_8),
                URLEncoder.encode(cuisineTypeValue, StandardCharsets.UTF_8)
        );

        String baseUrl = "https://api.edamam.com/api/recipes/v2";
        URL url = new URL(baseUrl + params);
        System.out.println("Request URL: " + url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Edamam-Account-User", "aryaman1");

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            // parse JSON and write out 'recipes.txt' as before
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root      = mapper.readTree(response.toString());

            List<String> outLines = new ArrayList<>();
            for (JsonNode hit : root.path("hits")) {
                JsonNode recipe   = hit.path("recipe");
                String   name     = recipe.path("label").asText();
                JsonNode ingLines = recipe.path("ingredientLines");

                outLines.add(name);
                for (JsonNode ing : ingLines) {
                    outLines.add("  - " + ing.asText());
                }
                outLines.add("");
            }

            Path outFile = Paths.get("recipes.txt");
            Files.write(
                    outFile, outLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println("Saved " + outLines.size() +
                    " lines to " + outFile.toAbsolutePath());
        }
    }
}
