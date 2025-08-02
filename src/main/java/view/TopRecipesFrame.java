package view;

import entity.RegularUser;
import org.example.MealPreferences;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads recipes from a text file, selects the three blocks
 * with the most hyphens (‘–’), writes them out to top_recipes.txt,
 * then displays them in a Swing frame.
 */
public class TopRecipesFrame {
    private static final String RECIPES_FILE    = "recipes.txt";
    private static final String OUTPUT_FILENAME = "top_recipes.txt";

    public static void main(RegularUser user) {
        SwingUtilities.invokeLater(() -> {
            Map<String,Integer> counts = loadAndCountRecipes(RECIPES_FILE);

            // If no recipes at all, show error and quit
            if (counts.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "No recipes found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(0);
            }

            // Pick top 3 by descending dash‑count
            List<Map.Entry<String,Integer>> top3 = counts.entrySet().stream()
                    .sorted(Comparator.<Map.Entry<String,Integer>>comparingInt(Map.Entry::getValue).reversed())
                    .limit(3)
                    .collect(Collectors.toList());

            // Auto‑save them out
            try {
                List<String> flattened = top3.stream()
                        .flatMap(e -> Arrays.stream(e.getKey().split("\\R")))
                        .collect(Collectors.toList());
                saveTopRecipes(flattened, OUTPUT_FILENAME);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to write top recipes:\n" + ioe.getMessage(),
                        "I/O Error",
                        JOptionPane.ERROR_MESSAGE
                );
                // continue on to show UI anyway
            }

            // Build and show the UI
            JFrame frame = new JFrame("Smart Plate Planner – Top 3 Recipes");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout(10,10));
            frame.setSize(700, 450);

            // Titles across top
            JPanel titleRow = new JPanel(new GridLayout(1, 3, 10, 0));
            for (Map.Entry<String,Integer> entry : top3) {
                JLabel lbl = new JLabel("<html><b>" + escapeHtml(entry.getKey().lines().findFirst().orElse("…")) + "</b></html>", SwingConstants.CENTER);
                lbl.setFont(lbl.getFont().deriveFont(16f));
                titleRow.add(lbl);
            }
            frame.add(titleRow, BorderLayout.NORTH);

            // Full recipe blocks
            JPanel recipePanel = new JPanel(new GridLayout(1, 3, 10, 0));
            for (Map.Entry<String,Integer> entry : top3) {
                JTextArea ta = new JTextArea(entry.getKey());
                ta.setEditable(false);
                ta.setLineWrap(true);
                ta.setWrapStyleWord(true);
                recipePanel.add(new JScrollPane(ta));
            }
            frame.add(recipePanel, BorderLayout.CENTER);
            JButton back = new JButton("Back to Meal Preferences");
            back.addActionListener(e -> {
                frame.dispose();
                new MealPreferences(user);
            });

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
            bottom.add(back);
            frame.add(bottom, BorderLayout.SOUTH);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        });
    }

    /** Reads the file, splits on blank lines, and counts '-' in each block. */
    private static Map<String,Integer> loadAndCountRecipes(String filename) {
        try {
            String all = Files.readString(Path.of(filename), StandardCharsets.UTF_8);
            // split into blocks wherever there are two or more newlines
            String[] blocks = all.split("\\R{2,}");
            Map<String,Integer> map = new LinkedHashMap<>();
            for (String block : blocks) {
                int dashCount = (int) block.chars().filter(ch -> ch == '-').count();
                map.put(block, dashCount);
            }
            return map;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "No recipes found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(0);
            return Collections.emptyMap(); // never reached
        }
    }

    /** Writes the given lines to the named file, overwriting any existing content. */
    private static void saveTopRecipes(List<String> lines, String outputFile) throws IOException {
        Path out = Paths.get(outputFile);
        Files.write(
                out,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /** Simple HTML-escape for display in a JLabel. */
    private static String escapeHtml(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}



//package view;
//
//import org.example.MongoConnectionDemo;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.border.TitledBorder;
//import java.awt.*;
//import java.io.IOException;
//import java.nio.file.*;
//import java.util.*;
//import java.util.List;
//import java.util.stream.*;
//
//public class TopRecipesFrame {
//    private static final String RECIPES_FILE = "recipes.txt";
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            List<String> recipes;
//            try {
//                recipes = loadRecipes(RECIPES_FILE);
//            } catch (IOException e) {
//                showErrorPage("Failed to read recipes file:\n" + e.getMessage());
//                return;
//            }
//
//            // Show whatever recipes are available (at most 3)
//            if (recipes.size() == 0) {
//                showErrorPage("Zero recipes found.\nPlease add more and try again.");
//                return;
//            }
//
//            // Count hyphens per recipe and pick the top 3 indices
//            Map<Integer, Integer> counts = new HashMap<>();
//            for (int i = 0; i < recipes.size(); i++) {
//                int c = (int) recipes.get(i).lines()
//                        .filter(line -> line.trim().startsWith("-"))
//                        .count();
//                counts.put(i, c);
//            }
//            List<Integer> top3 = counts.entrySet().stream()
//                    .sorted(Map.Entry.<Integer,Integer>comparingByValue(Comparator.reverseOrder()))
//                    .limit(3)
//                    .map(Map.Entry::getKey)
//                    .collect(Collectors.toList());
//
//            // Gather the full text of the top 3 recipes
//            List<String> topRecipes = top3.stream()
//                    .map(recipes::get)
//                    .collect(Collectors.toList());
//            showRecipes(topRecipes);
//        });
//    }
//
//    /** Reads the file and splits it into recipe blocks at blank lines. */
//    private static List<String> loadRecipes(String fileName) throws IOException {
//        List<String> all = Files.readAllLines(Paths.get(fileName));
//        List<String> blocks = new ArrayList<>();
//        StringBuilder sb = new StringBuilder();
//        for (String line : all) {
//            if (line.trim().isEmpty()) {
//                if (sb.length() > 0) {
//                    blocks.add(sb.toString().trim());
//                    sb.setLength(0);
//                }
//            } else {
//                sb.append(line).append("\n");
//            }
//        }
//        if (sb.length() > 0) {
//            blocks.add(sb.toString().trim());
//        }
//        return blocks;
//    }
//
//    /** Builds and shows the 3‑column recipe display. */
//    private static void showRecipes(List<String> topRecipes) {
//        JFrame frame = new JFrame("Top 3 Most Detailed Recipes");
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setSize(900, 600);
//        frame.setLayout(new BorderLayout(10,10));
//        ((JComponent)frame.getContentPane()).setBorder(new EmptyBorder(10,10,10,10));
//
//        // Titles
//        JPanel titlePanel = new JPanel(new GridLayout(1, 3, 10, 0));
//        for (String recipe : topRecipes) {
//            String title = recipe.contains("\n")
//                    ? recipe.substring(0, recipe.indexOf("\n"))
//                    : recipe;
//            JLabel lbl = new JLabel(title, SwingConstants.CENTER);
//            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));
//            lbl.setBorder(new EmptyBorder(5,5,5,5));
//            titlePanel.add(lbl);
//        }
//        frame.add(titlePanel, BorderLayout.NORTH);
//
//        // Full recipe
//        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 0));
//        for (String recipe : topRecipes) {
//            JTextArea area = new JTextArea(recipe);
//            area.setEditable(false);
//            area.setLineWrap(true);
//            area.setWrapStyleWord(true);
//            area.setFont(area.getFont().deriveFont(14f));
//            JScrollPane scroller = new JScrollPane(area);
//            scroller.setBorder(new TitledBorder("Details"));
//            contentPanel.add(scroller);
//        }
//        frame.add(contentPanel, BorderLayout.CENTER);
//
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
//
//    /** Shows a simple error window with the given message. */
//    private static void showErrorPage(String message) {
//        JFrame err = new JFrame("Error");
//        err.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        err.setSize(400, 200);
//        err.setLayout(new BorderLayout(10,10));
//        ((JComponent)err.getContentPane()).setBorder(new EmptyBorder(10,10,10,10));
//
//        JLabel lbl = new JLabel("<html><div style='text-align:center;'>" +
//                message.replaceAll("\n", "<br>") +
//                "</div></html>", SwingConstants.CENTER);
//        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
//        err.add(lbl, BorderLayout.CENTER);
//
//        JButton retry = new JButton("Try Again");
//        retry.addActionListener(ae -> err.dispose());
//        JPanel p = new JPanel();
//        p.add(retry);
//        err.add(p, BorderLayout.SOUTH);
//
//        err.setLocationRelativeTo(null);
//        err.setVisible(true);
//    }
//}
