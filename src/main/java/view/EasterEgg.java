package view;

import entity.RegularUser;
import view.MealPreferences;

import javax.swing.*;
import java.awt.*;

public class EasterEgg extends JFrame {
    public EasterEgg(RegularUser user) {
        setTitle("History");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300,300);
        JLabel title = new JLabel("Woof Woof How dare you");
        title.setFont(new Font("Times New Roman", Font.BOLD, 20));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setVerticalAlignment(JLabel.CENTER);
        add(title, BorderLayout.NORTH);

        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            dispose();
            new MealPreferences(user);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }
}
