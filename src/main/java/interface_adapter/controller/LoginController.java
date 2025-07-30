package interface_adapter.controller;

import use_case.login.LoginRequestModel;
import use_case.login.LoginUseCase;
import view.LoginView;

/**
 * Controller for handling user login interactions.
 */
public class LoginController {
    private final LoginUseCase interactor;
    private final LoginView    view;
    private final SignupController signupController;

    /**
     * @param interactor       The login use-case (whose login method is void)
     * @param view             The login view (frame)
     * @param signupController The signup controller to show the sign-up screen
     */
    public LoginController(LoginUseCase interactor,
                           LoginView view,
                           SignupController signupController) {
        this.interactor = interactor;
        this.view       = view;
        this.signupController = signupController;
    }

    /**
     * Called by LoginFrame when the user clicks 'Login'.
     * Invokes the interactor, which pushes results to the presenter.
     */
    public void login(String username, String password) {
        LoginRequestModel request = new LoginRequestModel(username, password);
        interactor.login(request);
    }

    /**
     * Called by LoginFrame when the user clicks 'Sign Up'.
     */
    public void onShowSignupRequested() {
        signupController.onShowSignupRequested();
    }
}

