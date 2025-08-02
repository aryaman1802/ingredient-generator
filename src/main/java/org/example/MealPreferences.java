package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

import app.Main;
import entity.RegularUser;
import view.Demo2;              // aryaman's code
import view.EasterEgg;
import view.HistoryFrame;
import view.TopRecipesFrame;    // aryaman's code

public class MealPreferences extends JFrame{

    /**
     * Creates a frame that takes in user meal preferences and has two buttons 'Submit' and History'.
     * the 'Submit' button gives that information to edamam API
     * the 'History' button leads to the history frame
     *
     * @param user the user that is logged in
     */
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
                " (Example: Milk, Egg, Apple)");
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

        //Meal type options
        String[] options = {"Breakfast", "Dinner", "Lunch", "Snack", "Teatime"};

        //Creates the first dropdown box for meal type options
        JComboBox<String> mealChoice = new JComboBox<>(options);
        mealChoice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(mealChoice);

        //Space to separate the fields
        panel.add(space);

        //Text that guide the reader
        JLabel CuisineType = new JLabel("What type of cuisine would you like?");
        CuisineType.setBorder(null);
        panel.add(CuisineType);

        //Cuisine type selection. These are all the options that are all the available inputs in the edamam API
        String[] CuisineOptions = {"American", "Asian", "British", "Caribbean", "Central Europe", "Chinese",
                "Eastern Europe", "French", "Greek", "Indian", "Italian", "Japanese", "Korean", "Kosher",
                "Mediterranean", "Mexican", "Middle Eastern", "Nordic", "South American", "South East Asian"};

        //Creates the second dropdown box for cuisine options
        JComboBox<String> cuisineChoice = new JComboBox<>(CuisineOptions);
        cuisineChoice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(cuisineChoice);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(panel);

        JPanel buttonPanel = new JPanel();

        //Adding history button and making it so that once clicked the history frame opens
        JButton history = new JButton("History");
        history.addActionListener(e -> {
            HistoryFrame historyFrame = new HistoryFrame(user);
            historyFrame.setVisible(true);
            dispose();
        });
        buttonPanel.add(history);

        buttonPanel.setLayout(new FlowLayout());

        // Button to submit preferences
        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> {
                String ingredients = textArea.getText();
                //Calls on helper method to format the string
                String preferences = getString(mealChoice, cuisineChoice, ingredients);
                //Easter egg
            if (ingredients.equals("Dog")) {
                    EasterEgg frame = new EasterEgg(user);
                    frame.setVisible(true);
                    dispose();
                }
            else{
                //Creates an output file called 'Preferences.txt' once user clicks submit
                try (FileWriter writer = new FileWriter("Preferences.txt")) {
                    writer.write(preferences);
                    System.out.println("Text successfully saved to 'Preferences.txt'.");
                } catch (IOException i) {
                    System.err.println("An error occurred while writing to the file: " + i.getMessage());
                }

                //Fetch new recipe
                try {
                    Demo2.main(new String[]{});
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //Show the top‐3 recipes based on user input
                TopRecipesFrame.main(user);
                MongoConnectionDemo.mealEntry(user.getUsername());
                dispose();
            }
            });
            buttonPanel.add(submit);

            // Logout button
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.addActionListener(e -> {
                dispose();
                Main.main(new String[]{});
            });
            buttonPanel.add(logoutBtn);

            add(buttonPanel, BorderLayout.SOUTH);
            setLocationRelativeTo(null);
            setVisible(true);


    }

    /**
     * Gets the input from the user in string format for better storage in text file
     * Split the string by commas because API needs to have inputs that are seperated by only commas no spaces
     * Outputs a refined string including properly spaced out ingredients, and meal and cuisine preferences
     *
     * @param mealChoice is what the user chose to be their meal type
     * @param cuisineChoice is what the user chose to be their cuisine type
     * @param ingredients is what ingredients the user typed to be included in the meal
     */
    private static String getString(JComboBox<String> mealChoice, JComboBox<String> cuisineChoice, String ingredients) {
        String mealType = (String) mealChoice.getSelectedItem();
        String cuisineType = (String) cuisineChoice.getSelectedItem();

        //Where we will store the string that has only commas and no space between ingredients
        StringBuilder stripped = new StringBuilder();

        //Split the string by commas
        String[] parts = ingredients.split(",");

        //Loop through the split array of strings and putting them back together in the format the API wants
        for (int i = 0; i < parts.length; i++) {
            stripped.append(parts[i].strip());
            if (i != parts.length - 1) {
                stripped.append(",");
            }
        }

        //The string returned will be a standard format so code can take the information to feed to the edamam API
        return String.format("%s%n%s%n%s",
                stripped,
                mealType,
                cuisineType);
    }

}
