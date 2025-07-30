package use_case.login;

import use_case.gateway.UserRepository;
import entity.RegularUser;
import java.util.Objects;
import java.util.Optional;

/**
 * Interactor for the Login use case.
 * Implements the business logic for authenticating a user.
 */
public class LoginInteractor implements LoginUseCase {
    private final UserRepository userRepository;
    private final LoginOutputBoundary presenter;

    public LoginInteractor(UserRepository userRepository,
                           LoginOutputBoundary presenter) {
        this.userRepository = userRepository;
        this.presenter = presenter;
    }

    @Override
    public void login(LoginRequestModel request) {
        Optional<RegularUser> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isPresent() &&
                verifyPassword(request.getPassword(), userOpt.get().getPasswordHash())) {
            presenter.present(new LoginResponseModel(
                    true,
                    "Login successful",
                    userOpt.get()
            ));
        } else {
            presenter.present(new LoginResponseModel(
                    false,
                    "Invalid username or password",
                    null
            ));
        }
    }

    /**
     * Stub for password verification. Replace with a real hash check later.
     */
    private boolean verifyPassword(String rawPassword, String storedHash) {
        return Objects.equals(rawPassword, storedHash);
    }
}

