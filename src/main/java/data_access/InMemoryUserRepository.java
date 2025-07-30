package data_access;

import use_case.gateway.UserRepository;
import entity.RegularUser;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of UserRepository for testing and development.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, RegularUser> users = new ConcurrentHashMap<>();

    @Override
    public Optional<RegularUser> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public void save(RegularUser user) {
        users.put(user.getUsername(), user);
    }
}
