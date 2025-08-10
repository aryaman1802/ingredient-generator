package view;

import app.Main;
import entity.RegularUser;
import org.example.MealPreferences;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A simple selector that lets the user choose which recipe source to use.
 * - "Edamam API" -> opens MealPreferences (your existing flow)
 * - "MealDB"     -> opens the TheMealDB UI (MealDBSwingApp.InputFrame)
 * - "Logout"     -> returns to Main (login)
 */
public class ApiChoiceFrame extends JFrame {

    private JButton edamamBtn;
    private JButton mealDbBtn;

    public ApiChoiceFrame(RegularUser user) {
        super("Choose Recipe Source");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 260);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        JLabel title = new JLabel("Pick an application", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        root.add(title, BorderLayout.NORTH);

        // Middle: two options
        JPanel columns = new JPanel(new GridLayout(1, 2, 10, 0));
        columns.add(makeOptionPanel(
                edamamBtn = makeSmallButton("Ingredient Only", () -> {
                    new MealPreferences(user).setVisible(true);
                    dispose();
                }),
                "<html><b><span style='color:#000'>Only generates ingredients</span></b></html>"
        ));
        columns.add(makeOptionPanel(
                mealDbBtn = makeSmallButton("Full Recipe", () -> {
                    new MealDBSwingApp.InputFrame().setVisible(true);
                    dispose();
                }),
                "<html><b><span style='color:#000'>Ingredients with Recipe</span></b></html>"
        ));
        root.add(columns, BorderLayout.CENTER);

        // Bottom: Logout button centered
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        south.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            Main.main(new String[]{});  // back to login
        });

        south.add(logoutBtn);
        root.add(south, BorderLayout.SOUTH);

        // Make Enter trigger the Edamam option by default
        getRootPane().setDefaultButton(edamamBtn);
    }

    private JPanel makeOptionPanel(JButton button, String subtitle) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel(subtitle, SwingConstants.CENTER);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 12f));
        sub.setForeground(new Color(0x666666));

        panel.add(Box.createVerticalStrut(8));
        panel.add(button);
        panel.add(Box.createVerticalStrut(8));
        panel.add(sub);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JButton makeSmallButton(String text, Runnable onClick) {
        JButton btn = new JButton(text);
        Dimension d = new Dimension(140, 32);
        btn.setPreferredSize(d);
        btn.setMaximumSize(d);
        btn.setMinimumSize(d);
        btn.setMargin(new Insets(4, 10, 4, 10));
        btn.addActionListener(e -> onClick.run());
        return btn;
    }
}





//package view;
//
//import entity.RegularUser;
//import org.example.MealPreferences;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//
///**
// * A simple selector that lets the user choose which recipe source to use.
// * - "Edamam API" -> opens MealPreferences (your existing flow)
// * - "MealDB"     -> opens the MealDB Swing UI (MealDBSwingApp.InputFrame)
// */
//public class ApiChoiceFrame extends JFrame {
//
//    public ApiChoiceFrame(RegularUser user) {
//        super("Choose Recipe Source");
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setSize(420, 200);
//        setLocationRelativeTo(null);
//
//        JPanel root = new JPanel(new BorderLayout(12, 12));
//        root.setBorder(new EmptyBorder(16, 16, 16, 16));
//        setContentPane(root);
//
//        JLabel title = new JLabel("Pick a recipe data source");
//        title.setHorizontalAlignment(SwingConstants.CENTER);
//        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
//        root.add(title, BorderLayout.NORTH);
//
//        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 12));
//        JButton edamamBtn = new JButton("Edamam API");
//        JButton mealDbBtn = new JButton("MealDB");
//
//        // Edamam path -> your existing MealPreferences screen
//        edamamBtn.addActionListener(e -> {
//            new MealPreferences(user).setVisible(true);
//            dispose();
//        });
//
//        // MealDB path -> open the TheMealDB UI
//        // NOTE: InputFrame is a static inner class inside MealDBSwingApp and is package-private.
//        // Because this class is in the same package (view), we can construct it directly.
//        mealDbBtn.addActionListener(e -> {
//            new MealDBSwingApp.InputFrame().setVisible(true);
//            dispose();
//        });
//
//        buttons.add(edamamBtn);
//        buttons.add(mealDbBtn);
//        root.add(buttons, BorderLayout.CENTER);
//
//        // Optional: make Enter activate the left button
//        getRootPane().setDefaultButton(edamamBtn);
//    }
//}


