package use_case.signup;

public class SignupRequestModel {
    private final String username;
    private final String password;

    public SignupRequestModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}

