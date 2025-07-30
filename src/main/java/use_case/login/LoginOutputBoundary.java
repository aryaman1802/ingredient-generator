package use_case.login;

/**
 * Output boundary for the Login use case.
 * Implementations handle displaying the result of a login attempt.
 */
public interface LoginOutputBoundary {
    /**
     * Present the results of the login attempt.
     *
     * @param response the LoginResponseModel containing success flag, message, and user data
     */
    void present(LoginResponseModel response);
}


