package use_case.login;

import entity.RegularUser;

public class LoginResponseModel {
    private final boolean success;
    private final String message;
    private final RegularUser user;  // will be null if login failed

    public LoginResponseModel(boolean success, String message, RegularUser user) {
        this.success = success;
        this.message = message;
        this.user    = user;
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * General-purpose message; on failure this is the error text.
     * For your controller you can also call getErrorMessage().
     */
    public String getMessage() {
        return message;
    }

    /**
     * Convenience alias so controller code can do getErrorMessage().
     */
    public String getErrorMessage() {
        return message;
    }

    public RegularUser getUser() {
        return user;
    }
}



