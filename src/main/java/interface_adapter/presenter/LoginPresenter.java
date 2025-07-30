package interface_adapter.presenter;

import use_case.login.LoginOutputBoundary;
import use_case.login.LoginResponseModel;
import view.LoginView;

public class LoginPresenter implements LoginOutputBoundary {
    private final LoginView view;

    public LoginPresenter(LoginView view) {
        this.view = view;
    }

    @Override
    public void present(LoginResponseModel response) {
        if (response.isSuccess()) {
            view.showLoginSuccess(response.getUser());
        } else {
            view.showLoginFailure(response.getMessage());
        }
    }
}