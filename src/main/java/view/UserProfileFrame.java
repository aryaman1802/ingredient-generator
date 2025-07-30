package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * SmartPlate Planner – User Profile frame.
 */
public class UserProfileFrame {
    public static void main(String[] args) {
        // Native look‑and‑feel
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            // --- Frame setup ---
            JFrame frame = new JFrame("SmartPlate Planner – User Profile");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(600, 350);
            frame.setLayout(new BorderLayout());

            // --- Header ---
            JLabel header = new JLabel("SmartPlate Planner", SwingConstants.CENTER);
            header.setFont(header.getFont().deriveFont(Font.BOLD, 22f));
            header.setBorder(new EmptyBorder(15, 0, 15, 0));
            frame.add(header, BorderLayout.NORTH);

            // --- Profile details panel ---
            JPanel infoPanel = new JPanel(new GridBagLayout());
            infoPanel.setBorder(new TitledBorder("Profile Details"));
            infoPanel.setBackground(Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 16, 8, 16);
            gbc.anchor = GridBagConstraints.WEST;

            // Row 0: Username
            gbc.gridx = 0;
            gbc.gridy = 0;
            infoPanel.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel("— placeholder —"), gbc);

            // Row 1: Food Allergies
            gbc.gridx = 0;
            gbc.gridy = 1;
            infoPanel.add(new JLabel("Food Allergies:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel("— placeholder —"), gbc);

            // Row 2: Cuisine Preferences
            gbc.gridx = 0;
            gbc.gridy = 2;
            infoPanel.add(new JLabel("Cuisine Preferences:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel("— placeholder —"), gbc);

            frame.add(infoPanel, BorderLayout.CENTER);

            // --- Buttons panel ---
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            buttonPanel.setBackground(Color.WHITE);

            JButton addProduceBtn = new JButton("Add Produce");
            addProduceBtn.setPreferredSize(new Dimension(140, 30));
            addProduceBtn.addActionListener((ActionEvent e) ->
                    JOptionPane.showMessageDialog(frame, "Add Produce clicked (stub)")
            );
            buttonPanel.add(addProduceBtn);

            JButton historyBtn = new JButton("History");
            historyBtn.setPreferredSize(new Dimension(140, 30));
            historyBtn.addActionListener((ActionEvent e) ->
                    JOptionPane.showMessageDialog(frame, "History clicked (stub)")
            );
            buttonPanel.add(historyBtn);

            frame.add(buttonPanel, BorderLayout.SOUTH);

            // --- Padding around the edges ---
            ((JComponent) frame.getContentPane())
                    .setBorder(new EmptyBorder(10, 10, 10, 10));

            // --- Show it ---
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}