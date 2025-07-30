package view;

import entity.RegularUser;
import org.example.MongoConnectionDemo;

import javax.swing.*;
import java.awt.*;

import static javax.swing.BoxLayout.Y_AXIS;

public class HistoryFrame extends JFrame {
    public HistoryFrame(RegularUser user) {
        //Making the main frame
        setTitle("History");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(150,300);
        setLayout(new BorderLayout());
        int size = MongoConnectionDemo.numEntry(user);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, Y_AXIS));

        for (int i = 0; i<size; i++) {
            JButton entry = new JButton("Entry" + i);
            entry.setSize(80, 30);
            buttonPanel.add(Box.createHorizontalStrut(10));
            buttonPanel.add(entry);
            int finalI = i;
            entry.addActionListener(e -> {
                MongoConnectionDemo.makeRecipe(user, finalI);
            });
        };
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.NORTH);
    }
}

