package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * MealDBSwingApp
 *
 * Build & run with Java 11+ (uses java.net.http) and Jackson on the classpath.
 * This program:
 *  1) Shows InputFrame -> collects ingredients, meal type, cuisine, diet, or triggers "Surprise Me".
 *  2) On "Search", writes Preferences.txt, calls Demo3.main(...) via reflection, then parses recipes.txt.
 *  3) Displays up to 3 recipes (columns) with name [diet], ingredients, and instructions.
 *  4) On "Surprise Me", calls TheMealDB random.php and shows exactly 1 recipe column, also showing cuisine.
 *
 * NOTE: Adjust look & feel or fonts to match your project.
 */
public class MealDBSwingApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setNiceDefaults();
            new InputFrame().setVisible(true);
        });
    }

    // ---------- Frame 1: Inputs ----------
    static class InputFrame extends JFrame {
        private final JTextField ingredientsField = new JTextField();
        private final JComboBox<String> mealTypeBox =
                new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner"});
        private final JComboBox<String> cuisineBox = new JComboBox<>();
//        private final JComboBox<String> dietBox =
//                new JComboBox<>(new String[]{"Vegetarian", "Non-Vegetarian", "Vegan"});
        private final JComboBox<String> dietBox =
                new JComboBox<>(new String[]{"None", "Vegetarian", "Non-Vegetarian", "Vegan"});
        private final JButton searchBtn = new JButton("Search Recipes");
        private final JButton surpriseBtn = new JButton("Surprise Me");

        private final JLabel status = new JLabel(" ");

        InputFrame() {
            super("Recipe Finder — TheMealDB");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(760, 420);
            setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout(12, 12));
            root.setBorder(new EmptyBorder(16, 16, 16, 16));
            setContentPane(root);

            JLabel title = new JLabel("Find recipes by ingredients, meal type, and cuisine");
            title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
            root.add(title, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            root.add(form, BorderLayout.CENTER);
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(8, 8, 8, 8);
            gc.fill = GridBagConstraints.HORIZONTAL;

            // Ingredients (with example)
            JLabel ingredientsLbl = new JLabel("Broad ingredients (comma-separated):");
            JLabel example = new JLabel("Example: chicken, garlic, onion");
            example.setFont(example.getFont().deriveFont(Font.ITALIC));
            gc.gridx = 0; gc.gridy = 0; gc.weightx = 0; form.add(ingredientsLbl, gc);
            gc.gridx = 1; gc.gridy = 0; gc.weightx = 1; form.add(ingredientsField, gc);
            gc.gridx = 1; gc.gridy = 1; gc.weightx = 1; form.add(example, gc);

            // Meal type
            JLabel mealLbl = new JLabel("Meal type:");
            gc.gridx = 0; gc.gridy = 2; gc.weightx = 0; form.add(mealLbl, gc);
            gc.gridx = 1; gc.gridy = 2; gc.weightx = 1; form.add(mealTypeBox, gc);

            // Cuisine
            JLabel cuisineLbl = new JLabel("Cuisine type:");
            gc.gridx = 0; gc.gridy = 3; gc.weightx = 0; form.add(cuisineLbl, gc);
            gc.gridx = 1; gc.gridy = 3; gc.weightx = 1; form.add(cuisineBox, gc);

            // Diet
            JLabel dietLbl = new JLabel("Dietary restriction:");
            gc.gridx = 0; gc.gridy = 4; gc.weightx = 0; form.add(dietLbl, gc);
            gc.gridx = 1; gc.gridy = 4; gc.weightx = 1; form.add(dietBox, gc);

//            // ========================= OLD CODE START =========================
//            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
//            actions.add(surpriseBtn);
//            actions.add(searchBtn);
//            root.add(actions, BorderLayout.SOUTH);
//            root.add(status, BorderLayout.PAGE_END);
//            status.setForeground(new Color(0x555555));
//            // ========================= OLD CODE END =========================

            // ========================= NEW CODE START =========================
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actions.add(surpriseBtn);
            actions.add(searchBtn);
            JPanel bottom = new JPanel(new BorderLayout(10, 0));
            bottom.add(status, BorderLayout.WEST);
            bottom.add(actions, BorderLayout.EAST);
            root.add(bottom, BorderLayout.SOUTH);

            status.setForeground(new Color(0x555555));
            status.setText("Ready. Enter ingredients or click “Surprise Me”.");
            // ========================= NEW CODE END =========================

            // Populate cuisines dynamically from TheMealDB (list.php?a=list), with fallback.
            loadCuisines();

            // Actions
            searchBtn.addActionListener(this::onSearch);
            surpriseBtn.addActionListener(this::onSurprise);

            // select None by default for dietary restrictions
            dietBox.setSelectedItem("None");
        }

        private void onSearch(ActionEvent e) {
            String query = ingredientsField.getText().trim();
            String mealType = (String) mealTypeBox.getSelectedItem();
            String cuisine = (String) cuisineBox.getSelectedItem();
            String diet = (String) dietBox.getSelectedItem();

            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter at least one ingredient or keyword.",
                        "Missing input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            searchBtn.setEnabled(false);
            surpriseBtn.setEnabled(false);
            status.setText("Searching TheMealDB via Demo3…");

            // Run the heavy work off the EDT
            new SwingWorker<List<Recipe>, Void>() {
                @Override protected List<Recipe> doInBackground() throws Exception {
                    // 1) Write Preferences.txt (3 lines as expected by Demo3)
                    Path prefs = Paths.get("Preferences.txt");
                    List<String> lines = List.of(query, mealType, cuisine);
                    Files.write(prefs, lines, StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                    // 2) Call Demo3.main(String[]), regardless of its package (via reflection).
                    try {
                        Class<?> demo3 = Class.forName("Demo3"); // if it’s in a package, adjust to "your.pkg.Demo3"
                        demo3.getMethod("main", String[].class)
                                .invoke(null, (Object) new String[]{}); // pass empty args
                    } catch (ClassNotFoundException cnf) {
                        // Try common package names you might have used; otherwise, rethrow
                        try {
                            Class<?> demo3 = Class.forName("view.Demo3");
                            demo3.getMethod("main", String[].class)
                                    .invoke(null, (Object) new String[]{});
                        } catch (Exception inner) {
                            throw new RuntimeException("Could not find Demo3.class on classpath. " +
                                    "Place MealDBSwingApp and Demo3 in the same project/module.", inner);
                        }
                    }

                    // 3) Parse recipes.txt written by Demo3
                    Path out = Paths.get("recipes.txt");
                    if (!Files.exists(out)) {
                        throw new FileNotFoundException("recipes.txt not found after Demo3 run.");
                    }
                    List<Recipe> all = parseRecipesTxt(Files.readAllLines(out, StandardCharsets.UTF_8));

                    // 4) Apply dietary filter & limit to top 3
                    Diet desired = Diet.fromLabel(diet);
                    List<Recipe> filtered = filterByDiet(all, desired);
                    if (filtered.size() > 3) filtered = filtered.subList(0, 3);
                    return filtered;
                }

                @Override protected void done() {
                    try {
                        List<Recipe> recipes = get();
                        if (recipes.isEmpty()) {
                            JOptionPane.showMessageDialog(InputFrame.this, "No recipes matched your criteria.",
                                    "No results", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            new ResultsFrame(recipes, /*isSurprise*/ false).setVisible(true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(InputFrame.this,
                                "Error: " + ex.getMessage(),
                                "Search failed", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        searchBtn.setEnabled(true);
                        surpriseBtn.setEnabled(true);
                        status.setText(" ");
                    }
                }
            }.execute();
        }

        private void onSurprise(ActionEvent e) {
            searchBtn.setEnabled(false);
            surpriseBtn.setEnabled(false);
            status.setText("Fetching a random recipe…");

            new SwingWorker<Recipe, Void>() {
                @Override protected Recipe doInBackground() throws Exception {
                    return fetchRandomRecipe(); // calls https://www.themealdb.com/api/json/v1/1/random.php
                }

                @Override protected void done() {
                    try {
                        Recipe r = get();
                        new ResultsFrame(List.of(r), /*isSurprise*/ true).setVisible(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(InputFrame.this,
                                "Failed to fetch a random recipe.\n" + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        searchBtn.setEnabled(true);
                        surpriseBtn.setEnabled(true);
                        status.setText(" ");
                    }
                }
            }.execute();
        }

        private void loadCuisines() {
            // Populate from API: list.php?a=list (Areas). If it fails, add a sensible fallback.
            cuisineBox.addItem("Any");
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder(
                        URI.create("https://www.themealdb.com/api/json/v1/1/list.php?a=list")).GET().build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                ObjectMapper om = new ObjectMapper();
                JsonNode root = om.readTree(resp.body());
                JsonNode arr = root.path("meals");
                if (arr.isArray()) {
                    for (JsonNode a : arr) {
                        String area = a.path("strArea").asText();
                        if (!area.isBlank()) cuisineBox.addItem(area);
                    }
                    return;
                }
            } catch (Exception ignored) { }
            // Fallback
            for (String a : List.of("American","British","Canadian","Chinese","Dutch","Egyptian","French","Greek",
                    "Indian","Irish","Italian","Jamaican","Japanese","Kenyan","Malaysian","Mexican","Moroccan",
                    "Polish","Portuguese","Russian","Spanish","Thai","Tunisian","Turkish","Vietnamese")) {
                cuisineBox.addItem(a);
            }
        }
    }

    // ---------- Frame 2: Results ----------
    static class ResultsFrame extends JFrame {
        ResultsFrame(List<Recipe> recipes, boolean isSurprise) {
            super(isSurprise ? "Surprise Recipe" : "Top Recipes");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(1000, 700);
            setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout(10, 10));
            root.setBorder(new EmptyBorder(12, 12, 12, 12));
            setContentPane(root);

            JLabel header = new JLabel(isSurprise ? "Here’s a random pick for you" : "Top recipe picks");
            header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
            root.add(header, BorderLayout.NORTH);

            JPanel columns = new JPanel(new GridLayout(1, recipes.size(), 12, 12));
            for (Recipe r : recipes) {
                columns.add(makeRecipeCard(r, isSurprise));
            }
            JScrollPane scroll = new JScrollPane(columns);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            root.add(scroll, BorderLayout.CENTER);
        }

        private JComponent makeRecipeCard(Recipe r, boolean isSurprise) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xDDDDDD)),
                    new EmptyBorder(12, 12, 12, 12)
            ));

            String titleTxt = r.name + "  [" + r.diet.label + "]";
            if (isSurprise && r.area != null && !r.area.isBlank()) {
                titleTxt += "  •  " + r.area;
            }
            JLabel title = new JLabel("<html><b>" + escapeHtml(titleTxt) + "</b></html>");
            title.setFont(title.getFont().deriveFont(16f));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(title);
            card.add(Box.createVerticalStrut(8));

            JLabel ingHeader = new JLabel("Ingredients");
            ingHeader.setFont(ingHeader.getFont().deriveFont(Font.BOLD));
            ingHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(ingHeader);

            JTextArea ingArea = new JTextArea(String.join("\n", prefix(r.ingredients, "• ")));
            ingArea.setEditable(false);
            ingArea.setLineWrap(true);
            ingArea.setWrapStyleWord(true);
            ingArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            JScrollPane ingScroll = new JScrollPane(ingArea);
            ingScroll.setPreferredSize(new Dimension(280, 160));
            ingScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(ingScroll);
            card.add(Box.createVerticalStrut(8));

            JLabel instrHeader = new JLabel("Instructions");
            instrHeader.setFont(instrHeader.getFont().deriveFont(Font.BOLD));
            instrHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(instrHeader);

            List<String> numbered = new ArrayList<>();
            for (int i = 0; i < r.instructions.size(); i++) {
                numbered.add((i + 1) + ". " + r.instructions.get(i));
            }
            JTextArea instArea = new JTextArea(String.join("\n\n", numbered));
            instArea.setEditable(false);
            instArea.setLineWrap(true);
            instArea.setWrapStyleWord(true);
            instArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            JScrollPane instScroll = new JScrollPane(instArea);
            instScroll.setPreferredSize(new Dimension(280, 300));
            instScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(instScroll);

            return card;
        }
    }

    // ---------- Data + parsing ----------
//    enum Diet {
//        VEGETARIAN("veg"), NON_VEGETARIAN("non-veg"), VEGAN("vegan");
//        public final String label;
//        Diet(String l) { this.label = l; }
//
//        static Diet fromLabel(String s) {
//            if (s == null) return NON_VEGETARIAN;
//            s = s.toLowerCase(Locale.ROOT);
//            if (s.startsWith("veg") && !s.contains("non")) return VEGETARIAN;
//            if (s.startsWith("vegan")) return VEGAN;
//            return NON_VEGETARIAN;
//        }
//    }
    enum Diet {
        NONE("none"), VEGETARIAN("veg"), NON_VEGETARIAN("non-veg"), VEGAN("vegan");
        public final String label;
        Diet(String l) { this.label = l; }

        static Diet fromLabel(String s) {
            if (s == null) return NONE;
            s = s.toLowerCase(Locale.ROOT);
            if (s.startsWith("none"))  return NONE;
            if (s.startsWith("vegan")) return VEGAN;
            if (s.startsWith("non"))   return NON_VEGETARIAN;
            if (s.startsWith("veg"))   return VEGETARIAN;
            return NONE;
        }
    }


    static class Recipe {
        String name;
        String area; // cuisine (only shown for Surprise)
        List<String> ingredients = new ArrayList<>();
        List<String> instructions = new ArrayList<>();
        Diet diet = Diet.NON_VEGETARIAN;
    }

    private static List<String> prefix(List<String> xs, String p) {
        List<String> out = new ArrayList<>(xs.size());
        for (String x : xs) out.add(p + x);
        return out;
    }

    // Parse recipes.txt written by Demo3:
    // Format expected:
    // Title
    //   - ingredient line
    //   - ingredient line
    // Instructions:
    // 1. step
    // 2. step
    // <blank line>
    private static List<Recipe> parseRecipesTxt(List<String> lines) {
        List<Recipe> out = new ArrayList<>();
        Recipe cur = null;
        boolean inInstr = false;
        for (String raw : lines) {
            String line = raw.strip();
            if (line.isEmpty()) {
                if (cur != null) {
                    // infer diet for the recipe before closing
                    cur.diet = inferDiet(cur.ingredients);
                    out.add(cur);
                    cur = null;
                    inInstr = false;
                }
                continue;
            }
            if (cur == null) {
                cur = new Recipe();
                cur.name = line;
                inInstr = false;
                continue;
            }
            if (line.equalsIgnoreCase("Instructions:")) {
                inInstr = true;
                continue;
            }
            if (!inInstr) {
                if (line.startsWith("-")) line = line.substring(1).trim();
                if (line.startsWith("•")) line = line.substring(1).trim();
                cur.ingredients.add(line);
            } else {
                // Remove leading "1. " / "2. " etc. if present
                cur.instructions.add(line.replaceFirst("^\\d+\\.?\\s*", "").trim());
            }
        }
        // flush last block if file doesn't end with blank line
        if (cur != null) {
            cur.diet = inferDiet(cur.ingredients);
            out.add(cur);
        }
        return out;
    }

//    private static List<Recipe> filterByDiet(List<Recipe> all, Diet desired) {
//        if (desired == Diet.NON_VEGETARIAN) return all;
//        List<Recipe> out = new ArrayList<>();
//        for (Recipe r : all) if (r.diet == desired) out.add(r);
//        return out;
//    }

    private static List<Recipe> filterByDiet(List<Recipe> all, Diet desired) {
        if (desired == Diet.NONE) return all;

        List<Recipe> out = new ArrayList<>();
        for (Recipe r : all) {
            switch (desired) {
                case VEGETARIAN:
                    // Vegetarian should also admit Vegan
                    if (r.diet == Diet.VEGETARIAN || r.diet == Diet.VEGAN) out.add(r);
                    break;
                case VEGAN:
                    if (r.diet == Diet.VEGAN) out.add(r);
                    break;
                case NON_VEGETARIAN:
                    // Tighten this to truly non-veg; “None” now covers the no-filter case
                    if (r.diet == Diet.NON_VEGETARIAN) out.add(r);
                    break;
                default:
                    out.add(r);
            }
        }
        return out;
    }


    // Heuristic dietary inference from ingredients
    private static Diet inferDiet(List<String> ingredients) {
        String blob = String.join(" | ", ingredients).toLowerCase(Locale.ROOT);

        // Obvious non-veg tokens (meat/seafood)
        String[] nonVeg = {
                "chicken","beef","pork","lamb","mutton","veal","turkey","duck","bacon","ham",
                "prosciutto","chorizo","sausage","meat","steak","mince","anchovy","fish","salmon",
                "tuna","cod","haddock","sardine","prawn","shrimp","crab","lobster","clam","mussel",
                "oyster","octopus","squid"
        };
        for (String t : nonVeg) if (blob.contains(t)) return Diet.NON_VEGETARIAN;

        // Non-vegan animal products
        String[] animalProducts = {
                "egg","eggs","milk","butter","ghee","cheese","yoghurt","yogurt","cream",
                "honey","gelatin","gelatine"
        };
        boolean hasAnimalProduct = false;
        for (String t : animalProducts) if (blob.contains(t)) { hasAnimalProduct = true; break; }

        return hasAnimalProduct ? Diet.VEGETARIAN : Diet.VEGAN;
    }

    // ---------- Surprise: call TheMealDB random.php and build a Recipe ----------
    private static Recipe fetchRandomRecipe() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(
                URI.create("https://www.themealdb.com/api/json/v1/1/random.php")).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        ObjectMapper om = new ObjectMapper();
        JsonNode root = om.readTree(resp.body());
        JsonNode m = root.path("meals").get(0);

        Recipe r = new Recipe();
        r.name = m.path("strMeal").asText();
        r.area = m.path("strArea").asText();

        // ingredients (1..20)
        for (int i = 1; i <= 20; i++) {
            String ing = m.path("strIngredient" + i).asText("").trim();
            String mea = m.path("strMeasure" + i).asText("").trim();
            if (ing.isEmpty()) break;
            String line = (mea.isEmpty() ? "" : mea + " ") + ing;
            r.ingredients.add(line.trim());
        }

        // instructions -> split to steps by blank lines / newlines / sentences
        String instr = m.path("strInstructions").asText("");
        r.instructions = splitSteps(instr);
        r.diet = inferDiet(r.ingredients);
        return r;
    }

    private static List<String> splitSteps(String instructions) {
        if (instructions == null) return List.of();
        String norm = instructions.replace("\r", "\n").trim();
        // Try blank-line split first
        String[] byPara = norm.split("\\n\\s*\\n+");
        List<String> steps = new ArrayList<>();
        if (byPara.length > 1) {
            for (String p : byPara) {
                String t = p.trim();
                if (!t.isEmpty()) steps.add(t);
            }
            return steps;
        }
        // Else split by single newlines
        String[] byNl = norm.split("\\n+");
        if (byNl.length > 1) {
            for (String p : byNl) {
                String t = p.trim();
                if (!t.isEmpty()) steps.add(t);
            }
            return steps;
        }
        // Fallback: sentence-ish
        String[] bySent = norm.split("(?<=\\.)\\s+");
        for (String s : bySent) {
            String t = s.trim();
            if (!t.isEmpty()) steps.add(t);
        }
        if (steps.isEmpty() && !norm.isEmpty()) steps.add(norm);
        return steps;
    }

    // ---------- UI niceties ----------
    private static void setNiceDefaults() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        UIManager.put("TextArea.font", new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    }

    private static String escapeHtml(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
