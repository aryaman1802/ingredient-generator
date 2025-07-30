package use_case.signup;

/**
 * Input boundary for the Signup use case.
 * Declares the method for handling signup requests.
 */
public interface SignupUseCase {
    /**
     * Process a signup request to register a new user.
     *
     * @param request the signup request model containing user credentials
     */
    void signup(SignupRequestModel request);
}

