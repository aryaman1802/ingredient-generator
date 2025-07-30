package use_case.login;

public interface LoginUseCase {
    /**
     * Attempt to log in with the provided credentials.
     *
     * @param request a LoginRequestModel containing username and password
     */
    void login(LoginRequestModel request);
}

