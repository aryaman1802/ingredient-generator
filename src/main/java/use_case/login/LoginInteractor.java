package use_case.login;

import use_case.gateway.UserRepository;
import entity.RegularUser;
import javax.swing.JOptionPane;
import java.util.Objects;
import java.util.Optional;

/**
 * Interactor for the Login use case.
 * Authenticates a user.
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
            // Show error popup
            JOptionPane.showMessageDialog(
                    null,
                    "Invalid username or password",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        }
    }

    /**
     * Password verification.
     */
    private boolean verifyPassword(String rawPassword, String storedHash) {
        return Objects.equals(rawPassword, storedHash);
    }
}

