package use_case.signup;

public interface SignupOutputBoundary {
    /** Called on successful signup, returns (or transforms) the success model. */
    SignupResponseModel prepareSuccessView(SignupResponseModel successModel);

    /** Called on failure (e.g. username taken), returns (or transforms) the failure model. */
    SignupResponseModel prepareFailureView(SignupResponseModel failureModel);
}
