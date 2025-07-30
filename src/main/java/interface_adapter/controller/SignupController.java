package interface_adapter.controller;

import javax.swing.SwingUtilities;
import use_case.signup.SignupRequestModel;
import use_case.signup.SignupUseCase;
import view.SignupView;
import view.LoginView;

/**
 * Controller for handling user sign-up interactions.
 */
public class SignupController {

    private final SignupUseCase interactor;
    private final SignupView   signupView;
    private final LoginView    loginView;

    /**
     * @param interactor   The sign-up use case
     * @param signupView   The sign-up frame/view
     * @param loginView    The login frame/view to return to
     */
    public SignupController(SignupUseCase interactor,
                            SignupView   signupView,
                            LoginView    loginView) {
        this.interactor = interactor;
        this.signupView = signupView;
        this.loginView  = loginView;
    }

    /**
     * Show the sign-up window and hide the login window.
     */
    public void onShowSignupRequested() {
        SwingUtilities.invokeLater(() -> {
            signupView.getFrame().setVisible(true);
            loginView.getFrame().setVisible(false);
        });
    }

    /**
     * Handle “Create Account” clicks from the sign-up form.
     * Validates mandatory fields, then invokes the use case.
     */
    public void onCreateAccount(String username, String password) {
        // Validate mandatory fields
        if (username == null || username.trim().isEmpty()) {
            signupView.showSignupFailure("Please enter a username.");
            return;
        }
        if (password == null || password.isEmpty()) {
            signupView.showSignupFailure("Please enter a password.");
            return;
        }

        // Build the request and dispatch
        SignupRequestModel request = new SignupRequestModel(username.trim(), password);
        interactor.signup(request);
    }
}




