package interface_adapter.presenter;

import org.example.MongoConnectionDemo;
import use_case.signup.SignupOutputBoundary;
import use_case.signup.SignupResponseModel;
import view.SignupView;
import view.LoginView;

/**
 * Presenter for the Signup use-case. Shows result on UI and redirects to login on success.
 */
public class SignupPresenter implements SignupOutputBoundary {
    private final SignupView signupView;
    private final LoginView  loginView;

    /**
     * @param signupView  The sign-up form view
     * @param loginView   The login form view to return to
     */
    public SignupPresenter(SignupView signupView, LoginView loginView) {
        this.signupView = signupView;
        this.loginView  = loginView;

    }

    @Override
    public SignupResponseModel prepareSuccessView(SignupResponseModel successModel) {
        // Show confirmation dialog
        signupView.showSignupSuccess("Signup successful! You can now log in.");
        // After user clicks OK, swap frames
        signupView.getFrame().setVisible(false);
        loginView.getFrame().setVisible(true);
        return successModel;
    }

    @Override
    public SignupResponseModel prepareFailureView(SignupResponseModel failureModel) {
        signupView.showSignupFailure(failureModel.getErrorMessage());
        return failureModel;
    }
}
