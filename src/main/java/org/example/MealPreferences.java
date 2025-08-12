package org.example;

import app.Main; // only used if you keep other navs; safe to keep
import entity.RegularUser;
import view.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class MealPreferences extends JFrame {

    private final RegularUser user;

    public MealPreferences(RegularUser user) {
        super("Meal Preferences — Edamam");
        this.user = user;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 450);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        // Title (match MealDB style)
        JLabel title = new JLabel("Find a List of Ingredients by Meal Type and Cuisine");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        root.add(title, BorderLayout.NORTH);

        // --- Form (GridBagLayout like MealDB) ---
        JPanel form = new JPanel(new GridBagLayout());
        root.add(form, BorderLayout.CENTER);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Ingredients
        JLabel ingLabel = new JLabel("Broad Ingredients (comma-separated):");
        JTextField ingredientsField = new JTextField();
        JLabel example = new JLabel("Example: milk, egg, apple");
        example.setFont(example.getFont().deriveFont(Font.ITALIC));

        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0; form.add(ingLabel, gc);
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1; form.add(ingredientsField, gc);
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1; form.add(example, gc);

        // Meal type
        JLabel mealLbl = new JLabel("Meal Type:");
        String[] mealOptions = {"Breakfast", "Dinner", "Lunch", "Snack", "Teatime"};
        JComboBox<String> mealChoice = new JComboBox<>(mealOptions);

        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0; form.add(mealLbl, gc);
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1; form.add(mealChoice, gc);

        // Cuisine type
        JLabel cuisineLbl = new JLabel("Cuisine Type:");
        String[] cuisineOptions = {"American", "Asian", "British", "Caribbean", "Central Europe", "Chinese",
                "Eastern Europe", "French", "Greek", "Indian", "Italian", "Japanese", "Korean", "Kosher",
                "Mediterranean", "Mexican", "Middle Eastern", "Nordic", "South American", "South East Asian"};
        JComboBox<String> cuisineChoice = new JComboBox<>(cuisineOptions);

        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0; form.add(cuisineLbl, gc);
        gc.gridx = 1; gc.gridy = 3; gc.weightx = 1; form.add(cuisineChoice, gc);

        // --- Footer: status (centered) row + buttons row ---
        JPanel footer = new JPanel(new BorderLayout(8, 0));

        JLabel status = new JLabel("Enter Preferences and click \"Search Ingredients\".", SwingConstants.CENTER);
        status.setForeground(new Color(0x555555));
        JPanel statusRow = new JPanel(new BorderLayout());
        statusRow.add(status, BorderLayout.CENTER);
        statusRow.setBorder(new EmptyBorder(6, 0, 6, 0));
        footer.add(statusRow, BorderLayout.NORTH);

        // Buttons
        JButton historyBtn = new JButton("History");
        historyBtn.addActionListener(e -> {
            HistoryFrame historyFrame = new HistoryFrame(user);
            historyFrame.setVisible(true);
            dispose();
        });

        JButton backBtn = new JButton("Back to API Selection");
        backBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new ApiChoiceFrame(user).setVisible(true));
        });

        JButton submitBtn = new JButton("Search Ingredients");
        submitBtn.addActionListener(e -> {
            String ingredients = ingredientsField.getText();
            String preferences = getString(mealChoice, cuisineChoice, ingredients);

            // Easter egg
            if ("Dog".equals(ingredients)) {
                EasterEgg frame = new EasterEgg(user);
                frame.setVisible(true);
                dispose();
                return;
            }

            // Write Preferences.txt for the Edamam flow
            try (FileWriter writer = new FileWriter("Preferences.txt")) {
                writer.write(preferences);
                System.out.println("Text successfully saved to 'Preferences.txt'.");
            } catch (IOException i) {
                System.err.println("Error writing to file: " + i.getMessage());
            }

            // Trigger your existing Edamam pipeline
            try {
                Demo2.main(new String[]{});   // fetch new recipe
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Show results + log entry
            TopRecipesFrame.main(user);
            MongoConnectionDemo.mealEntry(user.getUsername());
            dispose();
        });

        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttonsRow.add(historyBtn);
        buttonsRow.add(backBtn);
        buttonsRow.add(submitBtn);

        footer.add(buttonsRow, BorderLayout.SOUTH);
        root.add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Build the three-line string Edamam expects:
     * ingredients, mealType, cuisineType
     */
    private static String getString(JComboBox<String> mealChoice, JComboBox<String> cuisineChoice, String ingredients) {
        String mealType = (String) mealChoice.getSelectedItem();
        String cuisineType = (String) cuisineChoice.getSelectedItem();

        // normalize comma-separated ingredients (no spaces)
        String[] parts = ingredients.split(",");
        StringBuilder stripped = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            stripped.append(parts[i].strip());
            if (i != parts.length - 1) stripped.append(",");
        }

        return String.format("%s%n%s%n%s", stripped, mealType, cuisineType);
    }
}




//package org.example;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.FileWriter;
//import java.io.IOException;
//
//import app.Main;
//import entity.RegularUser;
//import view.*;
//
//public class MealPreferences extends JFrame{
//
//    /**
//     * Creates a frame that takes in user meal preferences and has two buttons 'Submit' and History'.
//     * the 'Submit' button gives that information to edamam API
//     * the 'History' button leads to the history frame
//     *
//     * @param user the user that is logged in
//     */
//    public MealPreferences(RegularUser user) {
//        //Making the main frame
//        setTitle("Meal Preferences");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(666,333);
//        setLayout(new BorderLayout());
//
//        //Text that guides the reader
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        JLabel ingredient = new JLabel("Broad Ingredients (comma-separated)");
//        panel.add(ingredient);
//
//        //Text to input ingredients
//        JTextArea textArea = new JTextArea();
//        panel.add(textArea);
//        textArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
//        textArea.setBorder(BorderFactory.createLineBorder(Color.black));
//
//        //Space to separate the fields
//        JLabel space = new JLabel(" ");
//        panel.add(space);
//
//        //Text that guide the reader
//        JLabel MealType = new JLabel("Meal Type");
//        MealType.setBorder(null);
//        panel.add(MealType);
//
//        //Meal type options
//        String[] options = {"Breakfast", "Dinner", "Lunch", "Snack", "Teatime"};
//
//        //Creates the first dropdown box for meal type options
//        JComboBox<String> mealChoice = new JComboBox<>(options);
//        mealChoice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
//        panel.add(mealChoice);
//
//        //Space to separate the fields
//        panel.add(space);
//
//        //Text that guide the reader
//        JLabel CuisineType = new JLabel("Cuisine Type");
//        CuisineType.setBorder(null);
//        panel.add(CuisineType);
//
//        //Cuisine type selection. These are all the options that are all the available inputs in the edamam API
//        String[] CuisineOptions = {"American", "Asian", "British", "Caribbean", "Central Europe", "Chinese",
//                "Eastern Europe", "French", "Greek", "Indian", "Italian", "Japanese", "Korean", "Kosher",
//                "Mediterranean", "Mexican", "Middle Eastern", "Nordic", "South American", "South East Asian"};
//
//        //Creates the second dropdown box for cuisine options
//        JComboBox<String> cuisineChoice = new JComboBox<>(CuisineOptions);
//        cuisineChoice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
//        panel.add(cuisineChoice);
//        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//
//        add(panel);
//
//        JPanel buttonPanel = new JPanel();
//
//        //Adding history button and making it so that once clicked the history frame opens
//        JButton history = new JButton("History");
//        history.addActionListener(e -> {
//            HistoryFrame historyFrame = new HistoryFrame(user);
//            historyFrame.setVisible(true);
//            dispose();
//        });
//        buttonPanel.add(history);
//
//        buttonPanel.setLayout(new FlowLayout());
//
//        // Button to submit preferences
//        JButton submit = new JButton("Submit");
//        submit.addActionListener(e -> {
//                String ingredients = textArea.getText();
//                //Calls on helper method to format the string
//                String preferences = getString(mealChoice, cuisineChoice, ingredients);
//                //Easter egg
//            if (ingredients.equals("Dog")) {
//                    EasterEgg frame = new EasterEgg(user);
//                    frame.setVisible(true);
//                    dispose();
//                }
//            else{
//                //Creates an output file called 'Preferences.txt' once user clicks submit
//                try (FileWriter writer = new FileWriter("Preferences.txt")) {
//                    writer.write(preferences);
//                    System.out.println("Text successfully saved to 'Preferences.txt'.");
//                } catch (IOException i) {
//                    System.err.println("An error occurred while writing to the file: " + i.getMessage());
//                }
//
//                //Fetch new recipe
//                try {
//                    Demo2.main(new String[]{});
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//
//                //Show the top‐3 recipes based on user input
//                TopRecipesFrame.main(user);
//                MongoConnectionDemo.mealEntry(user.getUsername());
//                dispose();
//            }
//            });
//            buttonPanel.add(submit);
//
//            // Back button
//
//        JButton backBtn = new JButton("Back to API Selection");
//        backBtn.addActionListener(e -> {
//            dispose(); // close this window
//            SwingUtilities.invokeLater(() -> new ApiChoiceFrame(user).setVisible(true));
//        });
//        buttonPanel.add(backBtn);
//
//            add(buttonPanel, BorderLayout.SOUTH);
//            setLocationRelativeTo(null);
//            setVisible(true);
//
//
//    }
//
//    /**
//     * Gets the input from the user in string format for better storage in text file
//     * Split the string by commas because API needs to have inputs that are seperated by only commas no spaces
//     * Outputs a refined string including properly spaced out ingredients, and meal and cuisine preferences
//     *
//     * @param mealChoice is what the user chose to be their meal type
//     * @param cuisineChoice is what the user chose to be their cuisine type
//     * @param ingredients is what ingredients the user typed to be included in the meal
//     */
//    private static String getString(JComboBox<String> mealChoice, JComboBox<String> cuisineChoice, String ingredients) {
//        String mealType = (String) mealChoice.getSelectedItem();
//        String cuisineType = (String) cuisineChoice.getSelectedItem();
//
//        //Where we will store the string that has only commas and no space between ingredients
//        StringBuilder stripped = new StringBuilder();
//
//        //Split the string by commas
//        String[] parts = ingredients.split(",");
//
//        //Loop through the split array of strings and putting them back together in the format the API wants
//        for (int i = 0; i < parts.length; i++) {
//            stripped.append(parts[i].strip());
//            if (i != parts.length - 1) {
//                stripped.append(",");
//            }
//        }
//
//        //The string returned will be a standard format so code can take the information to feed to the edamam API
//        return String.format("%s%n%s%n%s",
//                stripped,
//                mealType,
//                cuisineType);
//    }
//
//}
