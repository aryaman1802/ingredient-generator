package Test;

import entity.RegularUser;
import interface_adapter.controller.LoginController;
import interface_adapter.controller.SignupController;
import interface_adapter.presenter.LoginPresenter;
import interface_adapter.presenter.SignupPresenter;
import use_case.login.LoginRequestModel;
import use_case.login.LoginResponseModel;
import use_case.login.LoginUseCase;
import use_case.signup.SignupRequestModel;
import use_case.signup.SignupResponseModel;
import use_case.signup.SignupUseCase;
import view.LoginView;
import view.SignupView;

import javax.swing.JFrame;

/**
 * Test class for Interface Adapters layer - Controllers and Presenters
 *
 */
public class ControllerPresenterTest {

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // Mock Views for testing
    private static class MockLoginView implements LoginView {
        private boolean loginSuccessCalled = false;
        private boolean loginFailureCalled = false;
        private RegularUser lastUser = null;
        private String lastErrorMessage = null;
        private JFrame mockFrame = new JFrame();

        @Override
        public void showLoginSuccess(RegularUser user) {
            this.loginSuccessCalled = true;
            this.lastUser = user;
        }

        @Override
        public void showLoginFailure(String message) {
            this.loginFailureCalled = true;
            this.lastErrorMessage = message;
        }

        @Override
        public void prefillUsername(String username) {
            // Mock implementation - could store this for testing if needed
        }

        @Override
        public JFrame getFrame() {
            return mockFrame;
        }

        public boolean isLoginSuccessCalled() {
            return loginSuccessCalled;
        }

        public boolean isLoginFailureCalled() {
            return loginFailureCalled;
        }

        public RegularUser getLastUser() {
            return lastUser;
        }

        public String getLastErrorMessage() {
            return lastErrorMessage;
        }

        public void reset() {
            loginSuccessCalled = false;
            loginFailureCalled = false;
            lastUser = null;
            lastErrorMessage = null;
        }
    }

    private static class MockSignupView implements SignupView {
        private boolean signupSuccessCalled = false;
        private boolean signupFailureCalled = false;
        private String lastSuccessMessage = null;
        private String lastErrorMessage = null;
        private JFrame mockFrame = new JFrame();

        @Override
        public void showSignupSuccess(String message) {
            this.signupSuccessCalled = true;
            this.lastSuccessMessage = message;
        }

        @Override
        public void showSignupFailure(String message) {
            this.signupFailureCalled = true;
            this.lastErrorMessage = message;
        }

        @Override
        public JFrame getFrame() {
            return mockFrame;
        }

        public boolean isSignupSuccessCalled() {
            return signupSuccessCalled;
        }

        public boolean isSignupFailureCalled() {
            return signupFailureCalled;
        }

        public String getLastSuccessMessage() {
            return lastSuccessMessage;
        }

        public String getLastErrorMessage() {
            return lastErrorMessage;
        }

        public void reset() {
            signupSuccessCalled = false;
            signupFailureCalled = false;
            lastSuccessMessage = null;
            lastErrorMessage = null;
        }
    }

    // Mock Use Cases for testing
    private static class MockLoginUseCase implements LoginUseCase {
        private boolean shouldSucceed = true;
        private LoginRequestModel lastRequest = null;

        public void setShouldSucceed(boolean shouldSucceed) {
            this.shouldSucceed = shouldSucceed;
        }

        @Override
        public void login(LoginRequestModel request) {
            this.lastRequest = request;
            // This mock doesn't interact with presenter - just for testing controller logic
        }

        public LoginRequestModel getLastRequest() {
            return lastRequest;
        }
    }

    private static class MockSignupUseCase implements SignupUseCase {
        private SignupRequestModel lastRequest = null;

        @Override
        public void signup(SignupRequestModel request) {
            this.lastRequest = request;
            // This mock doesn't interact with presenter - just for testing controller logic
        }

        public SignupRequestModel getLastRequest() {
            return lastRequest;
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting Controller and Presenter Test execution...");

        runAllControllerPresenterTests();

        System.out.println("Controller and Presenter Test execution completed!");
        System.out.println("Total tests: " + totalTests);
        System.out.println("Passed tests: " + passedTests);
        System.out.println("Failed tests: " + failedTests);
        System.out.println("Success rate: " + (passedTests * 100.0 / totalTests) + "%");
    }

    private static void runAllControllerPresenterTests() {
        testLoginController();
        testSignupController();
        testLoginPresenter();
        testSignupPresenter();
    }

    // ================== LoginController Tests ==================

    private static void testLoginController() {
        System.out.println("\n--- LoginController Tests ---");

        testLoginControllerBasicLogin();
        testLoginControllerShowSignup();
    }

    private static void testLoginControllerBasicLogin() {
        try {
            MockLoginUseCase mockInteractor = new MockLoginUseCase();
            MockLoginView mockView = new MockLoginView();

            // Create a simple SignupController for testing
            MockSignupUseCase mockSignupUseCase = new MockSignupUseCase();
            MockSignupView mockSignupView = new MockSignupView();
            SignupController signupController = new SignupController(
                mockSignupUseCase, mockSignupView, mockView
            );

            LoginController controller = new LoginController(
                mockInteractor, mockView, signupController
            );

            // Test login functionality
            controller.login("testuser", "testpass");

            // Verify the controller passed correct data to interactor
            LoginRequestModel request = mockInteractor.getLastRequest();
            assertNotNull(request, "Request should be created");
            assertEquals("testuser", request.getUsername(), "Username should match");
            assertEquals("testpass", request.getPassword(), "Password should match");

            testPassed("LoginController basic login test");
        } catch (Exception e) {
            testFailed("LoginController basic login test", e.getMessage());
        }
    }

    private static void testLoginControllerShowSignup() {
        try {
            MockLoginUseCase mockInteractor = new MockLoginUseCase();
            MockLoginView mockView = new MockLoginView();

            // Create a simple SignupController for testing
            MockSignupUseCase mockSignupUseCase = new MockSignupUseCase();
            MockSignupView mockSignupView = new MockSignupView();
            SignupController signupController = new SignupController(
                mockSignupUseCase, mockSignupView, mockView
            );

            LoginController controller = new LoginController(
                mockInteractor, mockView, signupController
            );

            // Test show signup functionality - this should not throw exception
            controller.onShowSignupRequested();

            // The method should execute without error (we can't easily test UI visibility changes)
            testPassed("LoginController show signup test");
        } catch (Exception e) {
            testFailed("LoginController show signup test", e.getMessage());
        }
    }


    // ================== SignupController Tests ==================

    private static void testSignupController() {
        System.out.println("\n--- SignupController Tests ---");

        testSignupControllerValidInput();
        testSignupControllerEmptyUsername();
        testSignupControllerEmptyPassword();
        testSignupControllerShowSignup();
    }

    private static void testSignupControllerValidInput() {
        try {
            MockSignupUseCase mockInteractor = new MockSignupUseCase();
            MockSignupView mockSignupView = new MockSignupView();
            MockLoginView mockLoginView = new MockLoginView();

            SignupController controller = new SignupController(
                mockInteractor, mockSignupView, mockLoginView
            );

            // Test valid input
            controller.onCreateAccount("newuser", "newpass");

            // Verify the controller passed correct data to interactor
            SignupRequestModel request = mockInteractor.getLastRequest();
            assertNotNull(request, "Request should be created");
            assertEquals("newuser", request.getUsername(), "Username should match");
            assertEquals("newpass", request.getPassword(), "Password should match");

            // No failure message should be shown
            assertFalse(mockSignupView.isSignupFailureCalled(), "No failure message should be shown");

            testPassed("SignupController valid input test");
        } catch (Exception e) {
            testFailed("SignupController valid input test", e.getMessage());
        }
    }

    private static void testSignupControllerEmptyUsername() {
        try {
            MockSignupUseCase mockInteractor = new MockSignupUseCase();
            MockSignupView mockSignupView = new MockSignupView();
            MockLoginView mockLoginView = new MockLoginView();

            SignupController controller = new SignupController(
                mockInteractor, mockSignupView, mockLoginView
            );

            // Test empty username
            controller.onCreateAccount("", "password");

            // Verify failure message was shown
            assertTrue(mockSignupView.isSignupFailureCalled(), "Failure message should be shown");
            assertEquals("Please enter a username.", mockSignupView.getLastErrorMessage(), "Error message should match");

            // Verify interactor was not called
            assertNull(mockInteractor.getLastRequest(), "Interactor should not be called");

            testPassed("SignupController empty username test");
        } catch (Exception e) {
            testFailed("SignupController empty username test", e.getMessage());
        }
    }

    private static void testSignupControllerEmptyPassword() {
        try {
            MockSignupUseCase mockInteractor = new MockSignupUseCase();
            MockSignupView mockSignupView = new MockSignupView();
            MockLoginView mockLoginView = new MockLoginView();

            SignupController controller = new SignupController(
                mockInteractor, mockSignupView, mockLoginView
            );

            // Test empty password
            controller.onCreateAccount("username", "");

            // Verify failure message was shown
            assertTrue(mockSignupView.isSignupFailureCalled(), "Failure message should be shown");
            assertEquals("Please enter a password.", mockSignupView.getLastErrorMessage(), "Error message should match");

            // Verify interactor was not called
            assertNull(mockInteractor.getLastRequest(), "Interactor should not be called");

            testPassed("SignupController empty password test");
        } catch (Exception e) {
            testFailed("SignupController empty password test", e.getMessage());
        }
    }

    private static void testSignupControllerShowSignup() {
        try {
            MockSignupUseCase mockInteractor = new MockSignupUseCase();
            MockSignupView mockSignupView = new MockSignupView();
            MockLoginView mockLoginView = new MockLoginView();

            SignupController controller = new SignupController(
                mockInteractor, mockSignupView, mockLoginView
            );

            // Test show signup functionality
            boolean initialSignupVisible = mockSignupView.getFrame().isVisible();
            boolean initialLoginVisible = mockLoginView.getFrame().isVisible();

            controller.onShowSignupRequested();

            // Note: Due to SwingUtilities.invokeLater, we can't easily test visibility changes
            // in a unit test environment, but we can test that the method executes without exception
            testPassed("SignupController show signup test");
        } catch (Exception e) {
            testFailed("SignupController show signup test", e.getMessage());
        }
    }

    // ================== LoginPresenter Tests ==================

    private static void testLoginPresenter() {
        System.out.println("\n--- LoginPresenter Tests ---");

        testLoginPresenterSuccess();
        testLoginPresenterFailure();
    }

    private static void testLoginPresenterSuccess() {
        try {
            MockLoginView mockView = new MockLoginView();
            LoginPresenter presenter = new LoginPresenter(mockView);

            // Create successful response
            RegularUser user = new RegularUser("testuser", "testpass");
            LoginResponseModel successResponse = new LoginResponseModel(true, "Login successful", user);

            // Present successful response
            presenter.present(successResponse);

            // Verify success method was called
            assertTrue(mockView.isLoginSuccessCalled(), "Login success should be called");
            assertFalse(mockView.isLoginFailureCalled(), "Login failure should not be called");
            assertEquals(user, mockView.getLastUser(), "User should match");

            testPassed("LoginPresenter success test");
        } catch (Exception e) {
            testFailed("LoginPresenter success test", e.getMessage());
        }
    }

    private static void testLoginPresenterFailure() {
        try {
            MockLoginView mockView = new MockLoginView();
            LoginPresenter presenter = new LoginPresenter(mockView);

            // Create failure response
            LoginResponseModel failureResponse = new LoginResponseModel(false, "Invalid credentials", null);

            // Present failure response
            presenter.present(failureResponse);

            // Verify failure method was called
            assertTrue(mockView.isLoginFailureCalled(), "Login failure should be called");
            assertFalse(mockView.isLoginSuccessCalled(), "Login success should not be called");
            assertEquals("Invalid credentials", mockView.getLastErrorMessage(), "Error message should match");

            testPassed("LoginPresenter failure test");
        } catch (Exception e) {
            testFailed("LoginPresenter failure test", e.getMessage());
        }
    }

    // ================== SignupPresenter Tests ==================

    private static void testSignupPresenter() {
        System.out.println("\n--- SignupPresenter Tests ---");

        testSignupPresenterSuccess();
        testSignupPresenterFailure();
    }

    private static void testSignupPresenterSuccess() {
        try {
            MockSignupView mockSignupView = new MockSignupView();
            MockLoginView mockLoginView = new MockLoginView();
            SignupPresenter presenter = new SignupPresenter(mockSignupView, mockLoginView);

            // Create successful response
            SignupResponseModel successResponse = new SignupResponseModel(true, null);

            // Present successful response
            presenter.prepareSuccessView(successResponse);

            // Verify success method was called
            assertTrue(mockSignupView.isSignupSuccessCalled(), "Signup success should be called");
            assertFalse(mockSignupView.isSignupFailureCalled(), "Signup failure should not be called");
            assertTrue(mockSignupView.getLastSuccessMessage().contains("Signup successful"), "Success message should contain expected text");

            testPassed("SignupPresenter success test");
        } catch (Exception e) {
            testFailed("SignupPresenter success test", e.getMessage());
        }
    }

    private static void testSignupPresenterFailure() {
        try {
            MockSignupView mockSignupView = new MockSignupView();
            MockLoginView mockLoginView = new MockLoginView();
            SignupPresenter presenter = new SignupPresenter(mockSignupView, mockLoginView);

            // Create failure response
            SignupResponseModel failureResponse = new SignupResponseModel(false, "Username already exists");

            // Present failure response
            presenter.prepareFailureView(failureResponse);

            // Verify failure method was called
            assertTrue(mockSignupView.isSignupFailureCalled(), "Signup failure should be called");
            assertFalse(mockSignupView.isSignupSuccessCalled(), "Signup success should not be called");
            assertEquals("Username already exists", mockSignupView.getLastErrorMessage(), "Error message should match");

            testPassed("SignupPresenter failure test");
        } catch (Exception e) {
            testFailed("SignupPresenter failure test", e.getMessage());
        }
    }

    // ================== Helper Methods ==================

    private static void testPassed(String testName) {
        totalTests++;
        passedTests++;
        System.out.println("✅ " + testName + " - PASSED");
    }

    private static void testFailed(String testName, String errorMessage) {
        totalTests++;
        failedTests++;
        System.out.println("❌ " + testName + " - FAILED: " + errorMessage);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new AssertionError(message);
        }
    }

    private static void assertNull(Object object, String message) {
        if (object != null) {
            throw new AssertionError(message);
        }
    }
}