package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

import entity.RegularUser;
import view.Demo2;              // aryaman's code
import view.EasterEgg;
import view.HistoryFrame;
import view.TopRecipesFrame;    // aryaman's code

public class MealPreferences extends JFrame{

    public MealPreferences(RegularUser user) {
        //Making the main frame
        setTitle("Meal Preferences");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(666,333);
        setLayout(new BorderLayout());

        //Text that guides the reader
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel ingredient = new JLabel("What ingredients would like to include in your meal?" +
                " (Example: Milk,Egg,Apple)");
        panel.add(ingredient);

        //Text to input ingredients
        JTextArea textArea = new JTextArea();
        panel.add(textArea);
        textArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        textArea.setBorder(BorderFactory.createLineBorder(Color.black));

        //Space to separate the fields
        JLabel space = new JLabel(" ");
        panel.add(space);

        //Text that guide the reader
        JLabel MealType = new JLabel("Which meal would you like?");
        MealType.setBorder(null);
        panel.add(MealType);

        // Meal type selection
        String[] options = {"Breakfast", "Dinner", "Lunch", "Snack", "Teatime"};
        JComboBox<String> dropdown = new JComboBox<>(options);
        dropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(dropdown);

        //Space to separate the fields
        panel.add(space);

        //Text that guide the reader
        JLabel CuisineType = new JLabel("What type of cuisine would you like?");
        CuisineType.setBorder(null);
        panel.add(CuisineType);

        // Meal type selection
        String[] CuisineOptions = {"American", "Asian", "British", "Caribbean", "Central Europe", "Chinese",
                "Eastern Europe", "French", "Greek", "Indian", "Italian", "Japanese", "Korean", "Kosher",
                "Mediterranean", "Mexican", "Middle Eastern", "Nordic", "South American", "South East Asian"};
        JComboBox<String> dropdown1 = new JComboBox<>(CuisineOptions);
        dropdown1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(dropdown1);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        //Adding panel to frame
        add(panel);

        JPanel buttonPanel = new JPanel();
        JButton history = new JButton("History");

        history.addActionListener(e -> {
            // we’re already on the EDT here, so it’s safe to construct Swing components
            HistoryFrame historyFrame = new HistoryFrame(user);
            historyFrame.setVisible(true);
            dispose();
        });

        buttonPanel.add(history);
        buttonPanel.setLayout(new FlowLayout());
        JButton submit = new JButton("Submit");

        // Button to submit preferences
        submit.addActionListener(e -> {
                String ingredients = textArea.getText();
                String preferences = getString(dropdown, dropdown1, ingredients);
            if (ingredients.equals("Dog")) {
                    EasterEgg frame = new EasterEgg();
                    frame.setVisible(true);
                }
            else{
                try (FileWriter writer = new FileWriter("Preferences.txt")) {
                    writer.write(preferences);
                    System.out.println("Text successfully saved to 'Preferences.txt'.");
                } catch (IOException i) {
                    System.err.println("An error occurred while writing to the file: " + i.getMessage());
                }

                // new code by aryaman start
                // 1) Fetch new recipes
                try {
                    Demo2.main(new String[]{});
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // 2) Show the top‐3 recipes UI
                TopRecipesFrame.main(new String[]{});
                MongoConnectionDemo.mealEntry(user.getUsername());
                // new code by aryaman end

                dispose();
            }
            });
            buttonPanel.add(submit);

            add(buttonPanel, BorderLayout.SOUTH);

            setVisible(true);
    }

    private static String getString(JComboBox<String> dropdown, JComboBox<String> dropdown1, String ingredients) {
        String mealType = (String) dropdown.getSelectedItem();
        String cuisineType = (String) dropdown1.getSelectedItem();
        StringBuilder stripped = new StringBuilder();

        // Split the string by commas
        String[] parts = ingredients.split(",");

        // Print each part
        for (int i = 0; i < parts.length; i++) {
            stripped.append(parts[i].strip());
            if (i != parts.length - 1) {
                stripped.append(",");
            }
        }

        // For example, output to console, or handle as needed
        return String.format("%s%n%s%n%s",
                stripped,
                mealType,
                cuisineType);
    }

}
