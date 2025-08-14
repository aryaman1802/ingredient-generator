package view;

import entity.RegularUser;
import view.MealPreferences;

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
