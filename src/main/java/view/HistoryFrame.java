package view;

import entity.RegularUser;
import org.example.MealPreferences;
import org.example.MongoConnectionDemo;

import javax.swing.*;
import java.awt.*;

import static javax.swing.BoxLayout.Y_AXIS;

public class HistoryFrame extends JFrame {
    public HistoryFrame(RegularUser user) {
        //Making the main frame
        setTitle("History");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(200,300);
        setLayout(new BorderLayout());

        //Gets the amount of times the user has generated recipes
        int size = MongoConnectionDemo.numEntry(user);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, Y_AXIS));

        //Loops through all the timestamps of when the user generated recipes and setting that as the button text
        //Then Linking each button to retreave the recipe generated and display it using TopRecipes Frame
        for (int i = 0; i<size; i++) {
            JButton entry = new JButton(MongoConnectionDemo.timeStamp(user,i));
            entry.setSize(20, 30);
            buttonPanel.add(Box.createHorizontalStrut(20));
            buttonPanel.add(entry);
            int finalI = i;
            entry.addActionListener(e -> {
                MongoConnectionDemo.makeRecipe(user, finalI);
            });
        };

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            dispose();
            new MealPreferences(user);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        buttonPanel.setBorder(null);
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBorder(null);
        setLocationRelativeTo(null);
        add(scrollPane, BorderLayout.NORTH);
    }
}

