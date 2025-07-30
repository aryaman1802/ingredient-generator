package app;

import data_access.FileUserRepository;
import data_access.InMemoryUserRepository;
import interface_adapter.controller.LoginController;
import interface_adapter.controller.SignupController;
import interface_adapter.presenter.LoginPresenter;
import interface_adapter.presenter.SignupPresenter;
import org.example.MongoConnectionDemo;
import use_case.login.LoginInteractor;
import use_case.signup.SignupInteractor;
import view.LoginFrame;
import view.SignupFrame;

import javax.swing.SwingUtilities;

/**
 * Application entry point. Wires together Clean Architecture layers for login and sign-up.
 */
public class Main {
    public static void main(String[] args) {

        // Shared user repository
        FileUserRepository userRepo = new FileUserRepository();

        // Launch Swing UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // --- Views ---
            LoginFrame loginFrame   = new LoginFrame();
            SignupFrame signupFrame = new SignupFrame();

            // --- Presenters ---
            LoginPresenter  loginPresenter  = new LoginPresenter(loginFrame);
            SignupPresenter signupPresenter =
                    new SignupPresenter(signupFrame, loginFrame);


            // --- Interactors (Use Cases) ---
            LoginInteractor  loginInteractor  = new LoginInteractor(userRepo, loginPresenter);
            SignupInteractor signupInteractor = new SignupInteractor(userRepo, signupPresenter);

            // --- Controllers ---
            SignupController signupController =
                    new SignupController(signupInteractor, signupFrame, loginFrame);
            LoginController loginController   =
                    new LoginController(loginInteractor, loginFrame, signupController);

            // --- Inject controllers into views ---
            signupFrame.setController(signupController);
            loginFrame.setControllers(loginController, signupController);

            // Show the login window first
            loginFrame.getFrame().setVisible(true);
        });
    }
}


