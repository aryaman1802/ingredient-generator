//package view;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.io.*;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.*;
//import java.util.List;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import entity.RegularUser;
//import entity.Recipe;
//import entity.Diet;
////import use_case.recipe.RecipeService;
//import org.example.MongoMealDB;
//
//public class InputFrame extends JFrame {
//    private final RegularUser user;
//    private final JTextField ingredientsField = new JTextField();
//    private final JComboBox<String> mealTypeBox =
//            new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner"});
//    private final JComboBox<String> cuisineBox = new JComboBox<>();
//    private final JComboBox<String> dietBox =
//            new JComboBox<>(new String[]{"None", "Vegetarian", "Non-Vegetarian", "Vegan"});
//    private final JButton searchBtn = new JButton("Search Recipes");
//    private final JButton surpriseBtn = new JButton("Surprise Me");
//    private final JLabel status = new JLabel(" ");
//
//    public InputFrame(RegularUser user) {
//        super("Recipe Finder — TheMealDB");
//        this.user = user;
//
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(760, 420);
//        setLocationRelativeTo(null);
//
//        JPanel root = new JPanel(new BorderLayout(12, 12));
//        root.setBorder(new EmptyBorder(16, 16, 16, 16));
//        setContentPane(root);
//
//        JLabel title = new JLabel("Find Recipes by Ingredients, Meal Type, and Cuisine");
//        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
//        root.add(title, BorderLayout.NORTH);
//
//        JPanel form = new JPanel(new GridBagLayout());
//        root.add(form, BorderLayout.CENTER);
//        GridBagConstraints gc = new GridBagConstraints();
//        gc.insets = new Insets(8, 8, 8, 8);
//        gc.fill = GridBagConstraints.HORIZONTAL;
//
//        // Ingredients (with example)
//        JLabel ingredientsLbl = new JLabel("Broad Ingredients (comma-separated):");
//        JLabel example = new JLabel("Example: chicken, garlic, onion");
//        example.setFont(example.getFont().deriveFont(Font.ITALIC));
//        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0; form.add(ingredientsLbl, gc);
//        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1; form.add(ingredientsField, gc);
//        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1; form.add(example, gc);
//
//        // Meal type
//        JLabel mealLbl = new JLabel("Meal Type:");
//        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0; form.add(mealLbl, gc);
//        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1; form.add(mealTypeBox, gc);
//
//        // Cuisine
//        JLabel cuisineLbl = new JLabel("Cuisine Type:");
//        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0; form.add(cuisineLbl, gc);
//        gc.gridx = 1; gc.gridy = 3; gc.weightx = 1; form.add(cuisineBox, gc);
//
//        // Diet
//        JLabel dietLbl = new JLabel("Dietary Restriction:");
//        gc.gridx = 0; gc.gridy = 4; gc.weightx = 0; form.add(dietLbl, gc);
//        gc.gridx = 1; gc.gridy = 4; gc.weightx = 1; form.add(dietBox, gc);
//
//        // Footer
//        JPanel footer = new JPanel(new BorderLayout(8, 0));
//        status.setForeground(new Color(0x555555));
//        status.setText("Enter Ingredients or click \"Surprise Me\".");
//        status.setHorizontalAlignment(SwingConstants.CENTER);
//
//        JPanel statusRow = new JPanel(new BorderLayout());
//        statusRow.add(status, BorderLayout.CENTER);
//        statusRow.setBorder(new EmptyBorder(0, 0, 6, 0));
//        footer.add(statusRow, BorderLayout.NORTH);
//
//        JButton backBtn = new JButton("Back to API Selection");
//        backBtn.addActionListener(e -> {
//            dispose();
//            SwingUtilities.invokeLater(() -> new ApiChoiceFrame(user).setVisible(true));
//        });
//
//        JButton history = new JButton("History");
//        history.addActionListener(e -> {
//            RecipeHistory historyFrame = new RecipeHistory(user);
//            historyFrame.setVisible(true);
//            dispose();
//        });
//
//        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
//        buttonsRow.add(history);
//        buttonsRow.add(backBtn);
//        buttonsRow.add(surpriseBtn);
//        buttonsRow.add(searchBtn);
//        footer.add(buttonsRow, BorderLayout.SOUTH);
//
//        root.add(footer, BorderLayout.SOUTH);
//
//        loadCuisines();
//        searchBtn.addActionListener(this::onSearch);
//        surpriseBtn.addActionListener(this::onSurprise);
//        dietBox.setSelectedItem("None");
//    }
//
//    private void onSearch(ActionEvent e) {
//        String query = ingredientsField.getText().trim();
//        String mealType = (String) mealTypeBox.getSelectedItem();
//        String cuisine = (String) cuisineBox.getSelectedItem();
//        String diet = (String) dietBox.getSelectedItem();
//
//        if (query.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please enter at least one ingredient or keyword.",
//                    "Missing input", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        searchBtn.setEnabled(false);
//        surpriseBtn.setEnabled(false);
//        status.setText("Searching TheMealDB via Demo3…");
//
//        new SwingWorker<List<Recipe>, Void>() {
//            @Override protected List<Recipe> doInBackground() throws Exception {
//                Path prefs = Paths.get("Preferences.txt");
//                List<String> lines = List.of(query, mealType, cuisine);
//                Files.write(prefs, lines, StandardCharsets.UTF_8,
//                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//
//                try {
//                    Class<?> demo3 = Class.forName("Demo3");
//                    demo3.getMethod("main", String[].class)
//                            .invoke(null, (Object) new String[]{});
//                } catch (ClassNotFoundException cnf) {
//                    try {
//                        Class<?> demo3 = Class.forName("view.Demo3");
//                        demo3.getMethod("main", String[].class)
//                                .invoke(null, (Object) new String[]{});
//                    } catch (Exception inner) {
//                        throw new RuntimeException("Could not find Demo3.class on classpath.", inner);
//                    }
//                }
//
//                Path out = Paths.get("recipes.txt");
//                if (!Files.exists(out)) {
//                    throw new FileNotFoundException("recipes.txt not found after Demo3 run.");
//                }
//                List<Recipe> all = RecipeService.parseRecipesTxt(Files.readAllLines(out, StandardCharsets.UTF_8));
//                Diet desired = Diet.fromLabel(diet);
//                List<Recipe> filtered = RecipeService.filterByDiet(all, desired);
//                if (filtered.size() > 3) filtered = filtered.subList(0, 3);
//                return filtered;
//            }
//
//            @Override protected void done() {
//                try {
//                    List<Recipe> recipes = get();
//                    if (recipes.isEmpty()) {
//                        JOptionPane.showMessageDialog(InputFrame.this, "No recipes matched your criteria.",
//                                "No results", JOptionPane.INFORMATION_MESSAGE);
//                    } else {
//                        new ResultsFrame(recipes, false, user).setVisible(true);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(InputFrame.this,
//                            "Error: " + ex.getMessage(),
//                            "Search failed", JOptionPane.ERROR_MESSAGE);
//                } finally {
//                    searchBtn.setEnabled(true);
//                    surpriseBtn.setEnabled(true);
//                    status.setText(" ");
//                }
//            }
//        }.execute();
//        MongoMealDB.mealEntry(user.getUsername(), Boolean.FALSE);
//    }
//
//    private void onSurprise(ActionEvent e) {
//        searchBtn.setEnabled(false);
//        surpriseBtn.setEnabled(false);
//        status.setText("Fetching a random recipe…");
//
//        new SwingWorker<Recipe, Void>() {
//            @Override protected Recipe doInBackground() throws Exception {
//                return RecipeService.fetchRandomRecipe();
//            }
//
//            @Override protected void done() {
//                try {
//                    Recipe r = get();
//                    new ResultsFrame(List.of(r), true, user).setVisible(true);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(InputFrame.this,
//                            "Failed to fetch a random recipe.\n" + ex.getMessage(),
//                            "Error", JOptionPane.ERROR_MESSAGE);
//                } finally {
//                    searchBtn.setEnabled(true);
//                    surpriseBtn.setEnabled(true);
//                    status.setText(" ");
//                }
//            }
//        }.execute();
//        MongoMealDB.mealEntry(user.getUsername(), Boolean.TRUE);
//    }
//
//    private void loadCuisines() {
//        cuisineBox.addItem("Any");
//        try {
//            HttpClient client = HttpClient.newHttpClient();
//            HttpRequest req = HttpRequest.newBuilder(
//                    URI.create("https://www.themealdb.com/api/json/v1/1/list.php?a=list")).GET().build();
//            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
//            ObjectMapper om = new ObjectMapper();
//            JsonNode root = om.readTree(resp.body());
//            JsonNode arr = root.path("meals");
//            if (arr.isArray()) {
//                for (JsonNode a : arr) {
//                    String area = a.path("strArea").asText();
//                    if (!area.isBlank()) cuisineBox.addItem(area);
//                }
//                return;
//            }
//        } catch (Exception ignored) { }
//        // Fallback
//        for (String a : List.of("American","British","Canadian","Chinese","Dutch","Egyptian","French","Greek",
//                "Indian","Irish","Italian","Jamaican","Japanese","Kenyan","Malaysian","Mexican","Moroccan",
//                "Polish","Portuguese","Russian","Spanish","Thai","Tunisian","Turkish","Vietnamese")) {
//            cuisineBox.addItem(a);
//        }
//    }
//}
