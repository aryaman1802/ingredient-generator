package entity;

/**
 * Entity representing a registered user of the application.
 */
public class RegularUser {
    private final String username;
    private final String passwordHash;

    public RegularUser(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}

