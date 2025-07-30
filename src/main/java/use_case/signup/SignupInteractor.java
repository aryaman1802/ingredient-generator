package use_case.signup;

import use_case.gateway.UserRepository;
import entity.RegularUser;
import java.util.Optional;

/**
 * Interactor for the Signup use case.
 * Implements the business logic for registering a new user.
 */
public class SignupInteractor implements SignupUseCase {
    private final UserRepository userRepository;
    private final SignupOutputBoundary outputBoundary;

    /**
     * @param userRepository  Gateway for user persistence
     * @param outputBoundary  Presenter boundary for showing response
     */
    public SignupInteractor(UserRepository userRepository,
                            SignupOutputBoundary outputBoundary) {
        this.userRepository = userRepository;
        this.outputBoundary = outputBoundary;
    }

    /**
     * Processes the sign-up request, checking duplicates and saving new users,
     * then delegates to the presenter via the output boundary.
     */
    @Override
    public void signup(SignupRequestModel request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // Check for existing user
        Optional<RegularUser> existing = userRepository.findByUsername(username);
        if (existing.isPresent()) {
            SignupResponseModel failure = new SignupResponseModel(
                    false,
                    "Username '" + username + "' is already taken."
            );
            outputBoundary.prepareFailureView(failure);
            return;
        }
        // Notify success
        SignupResponseModel success = new SignupResponseModel(
                true,
                null
        );
        outputBoundary.prepareSuccessView(success);

        // Create and save the new user
        RegularUser newUser = new RegularUser(username, password);
        userRepository.save(newUser);


    }
}
