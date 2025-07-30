package view;

import entity.RegularUser;
import interface_adapter.controller.LoginController;
import interface_adapter.controller.SignupController;
import org.example.MealPreferences;

import javax.swing.*;
import java.awt.*;

/**
 * Swing frame for user login.
 * Implements LoginView to receive presenter callbacks.
 */
public class LoginFrame extends JFrame implements LoginView {
    private LoginController loginController;
    private SignupController signupController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel messageLabel;

    /**
     * No-argument constructor for app initialization. Use setControllers() to inject the controllers.
     */
    public LoginFrame() {
        initComponents();
    }

    /**
     * Inject both LoginController and SignupController after creating the frame.
     */
    public void setControllers(LoginController loginController,
                               SignupController signupController) {
        this.loginController  = loginController;
        this.signupController = signupController;
        loginButton.addActionListener(e -> onLogin());
        signupButton.addActionListener(e -> onShowSignup());
    }

    private void initComponents() {
        setTitle("Smart Plate Planner - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title label in bold at the top
        JLabel titleLabel = new JLabel("Smart Plate Planner", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        add(titleLabel, BorderLayout.NORTH);

        // Main form panel
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username label and field
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Buttons: Login and Sign Up side by side
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton  = new JButton("Login");
        signupButton = new JButton("Sign Up");
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        panel.add(buttonPanel, gbc);

        // Message label for errors
        gbc.gridy = 3;
        messageLabel = new JLabel();
        messageLabel.setForeground(Color.RED);
        panel.add(messageLabel, gbc);

        // Attach panel
        add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void onLogin() {
        messageLabel.setText("");
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (loginController != null) {
            loginController.login(username, password);
        } else {
            throw new IllegalStateException("LoginController has not been set.");
        }
    }

    private void onShowSignup() {
        if (signupController != null) {
            signupController.onShowSignupRequested();
        } else {
            throw new IllegalStateException("SignupController has not been set.");
        }
    }

    // --- LoginView methods ---

    @Override
    public void showLoginSuccess(RegularUser user) {
        // Close the login window
        this.dispose();
        // Open the MealPreferences window
        SwingUtilities.invokeLater(() -> {
            MealPreferences frame = new MealPreferences(user);
            frame.setVisible(true);
        });
        System.out.println("MealPreferences has been created.");
//        SwingUtilities.invokeLater(Demo2::new);
//        System.out.println("Demo2 has been created.");
    }

    @Override
    public void showLoginFailure(String message) {
        messageLabel.setText(message);
    }

    @Override
    public void prefillUsername(String username) {
        usernameField.setText(username);
    }

    @Override
    public JFrame getFrame() {
        return this;
    }
}



//package view;
//
//import entity.RegularUser;
//import interface_adapter.controller.LoginController;
//import interface_adapter.controller.SignupController;
//
//import javax.swing.*;
//import java.awt.*;
//
///**
// * Swing frame for user login.
// * Implements LoginView to receive presenter callbacks.
// */
//public class LoginFrame extends JFrame implements LoginView {
//    private LoginController loginController;
//    private SignupController signupController;
//    private JTextField usernameField;
//    private JPasswordField passwordField;
//    private JButton loginButton;
//    private JButton signupButton;
//    private JLabel messageLabel;
//
//    /**
//     * No-argument constructor for app initialization. Use setControllers() to inject the controllers.
//     */
//    public LoginFrame() {
//        initComponents();
//    }
//
//    /**
//     * Inject both LoginController and SignupController after creating the frame.
//     */
//    public void setControllers(LoginController loginController,
//                               SignupController signupController) {
//        this.loginController  = loginController;
//        this.signupController = signupController;
//        loginButton.addActionListener(e -> onLogin());
//        signupButton.addActionListener(e -> onShowSignup());
//    }
//
//    private void initComponents() {
//        setTitle("Smart Plate Planner - Login");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLayout(new BorderLayout());
//
//        // Title label in bold at the top
//        JLabel titleLabel = new JLabel("Smart Plate Planner", SwingConstants.CENTER);
//        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
//        add(titleLabel, BorderLayout.NORTH);
//
//        // Main form panel
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(8, 8, 8, 8);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        // Username label and field
//        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
//        panel.add(new JLabel("Username:"), gbc);
//        gbc.gridx = 1;
//        usernameField = new JTextField(15);
//        panel.add(usernameField, gbc);
//
//        // Password label and field
//        gbc.gridx = 0; gbc.gridy = 1;
//        panel.add(new JLabel("Password:"), gbc);
//        gbc.gridx = 1;
//        passwordField = new JPasswordField(15);
//        panel.add(passwordField, gbc);
//
//        // Buttons: Login and Sign Up side by side
//        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
//        loginButton  = new JButton("Login");
//        signupButton = new JButton("Sign Up");
//        buttonPanel.add(loginButton);
//        buttonPanel.add(signupButton);
//        panel.add(buttonPanel, gbc);
//
//        // Message label for errors
//        gbc.gridy = 3;
//        messageLabel = new JLabel();
//        messageLabel.setForeground(Color.RED);
//        panel.add(messageLabel, gbc);
//
//        // Attach panel
//        add(panel, BorderLayout.CENTER);
//
//        pack();
//        setLocationRelativeTo(null);
//    }
//
//    private void onLogin() {
//        messageLabel.setText("");
//        String username = usernameField.getText().trim();
//        String password = new String(passwordField.getPassword());
//        if (loginController != null) {
//            loginController.login(username, password);
//        } else {
//            throw new IllegalStateException("LoginController has not been set.");
//        }
//    }
//
//    private void onShowSignup() {
//        if (signupController != null) {
//            signupController.onShowSignupRequested();
//        } else {
//            throw new IllegalStateException("SignupController has not been set.");
//        }
//    }
//
//    // --- LoginView methods ---
//
//    @Override
//    public void showLoginSuccess(RegularUser user) {
//        // Hide the login window
//        this.setVisible(false);
//        // Redirect to the user profile page
//        SwingUtilities.invokeLater(() -> {
//            // Launch the UserProfileFrame
//            UserProfileFrame.main(new String[]{});
//        });
//    }
//
//    @Override
//    public void showLoginFailure(String message) {
//        messageLabel.setText(message);
//    }
//
//    @Override
//    public void prefillUsername(String username) {
//        usernameField.setText(username);
//    }
//
//    @Override
//    public JFrame getFrame() {
//        return this;
//    }
//}


