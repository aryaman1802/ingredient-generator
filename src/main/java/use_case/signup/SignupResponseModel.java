package use_case.signup;

public class SignupResponseModel {
    private final boolean success;
    private final String errorMessage;  // null on success

    public SignupResponseModel(boolean success, String errorMessage) {
        this.success      = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess()          { return success; }
    public String getErrorMessage()    { return errorMessage; }
}

