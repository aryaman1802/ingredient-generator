package use_case.gateway;

import java.util.Optional;
import entity.RegularUser;

public interface UserRepository {
    Optional<RegularUser> findByUsername(String username);
    void save(RegularUser user);
}
