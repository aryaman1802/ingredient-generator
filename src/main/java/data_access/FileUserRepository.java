package data_access;

import org.example.MongoConnectionDemo;
import use_case.gateway.UserRepository;
import entity.RegularUser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

/**
 * File-based implementation of UserRepository.
 * Saves each user's credentials to its own CSV file under a "users" directory.
 * CSV format per file: username,password
 */
public class FileUserRepository implements UserRepository {
    private final Path usersDir;

    public FileUserRepository() {
        usersDir = Paths.get("users");
        try {
            Files.createDirectories(usersDir);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create users directory", e);
        }
    }

    @Override
    public Optional<RegularUser> findByUsername(String username) {
        Path userFile = usersDir.resolve(username + ".csv");
        if (!Files.exists(userFile)) {
            return Optional.empty();
        }
        try {
            String line = Files.readString(userFile).trim();
            String[] parts = line.split(",", -1);
            if (parts.length < 2) {
                return Optional.empty();
            }
            String user = parts[0];
            String pass = parts[1];
            return Optional.of(new RegularUser(user, pass));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read user file for " + username, e);
        }
    }

    @Override
    public void save(RegularUser user) {
        Path userFile = usersDir.resolve(user.getUsername() + ".csv");
        String line = user.getUsername() + "," + user.getPasswordHash();
        try {
            Files.writeString(
                    userFile,
                    line,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user " + user.getUsername(), e);
        }
        MongoConnectionDemo.newuser(user.getUsername(), user.getPasswordHash());
    }
}
