package view;

import interface_adapter.controller.SignupController;
import javax.swing.*;
import java.awt.*;

/**
 * Swing frame for the Smart Plate Planner sign-up form.
 * Implements SignupView to receive presenter callbacks.
 */
public class SignupFrame extends JFrame implements SignupView {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton createBtn;

    /**
     * No-arg constructor. Use setController() to inject the SignupController.
     */
    public SignupFrame() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        createBtn     = new JButton("Create Account");
        initUI();
    }

    /**
     * Inject the controller after frame construction.
     */
    public void setController(SignupController controller) {
        createBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());
            controller.onCreateAccount(user, pass);
        });
    }

    private void initUI() {
        setTitle("Smart Plate Planner - Sign Up");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title label in bold at the top
        JLabel titleLabel = new JLabel("Smart Plate Planner", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        add(titleLabel, BorderLayout.NORTH);

        // Form panel in the center
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Create Account button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createBtn, gbc);

        // Attach panel
        add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    // --- SignupView methods ---

    @Override
    public void showSignupSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showSignupFailure(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public JFrame getFrame() {
        return this;
    }
}

