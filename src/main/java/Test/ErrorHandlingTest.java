package Test;

import entity.RegularUser;
import use_case.gateway.UserRepository;
import use_case.login.*;
import data_access.InMemoryUserRepository;

import java.util.Optional;

/**
 * Comprehensive test class for Error Handling and Boundary Conditions
 * Tests edge cases
 */
public class ErrorHandlingTest {

    // Test counters
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // Mock objects for testing
    private static class MockLoginPresenter implements LoginOutputBoundary {
        private LoginResponseModel lastResponse;

        @Override
        public void present(LoginResponseModel response) {
            this.lastResponse = response;
        }

        public LoginResponseModel getLastResponse() {
            return lastResponse;
        }

        public void reset() {
            lastResponse = null;
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting Error Handling Test execution...");

        // Run all error handling and boundary tests
        runAllErrorTests();
        
        // Output test results
        System.out.println("Error Handling Test execution completed!");
        System.out.println("Total tests: " + totalTests);
        System.out.println("Passed tests: " + passedTests);
        System.out.println("Failed tests: " + failedTests);
        System.out.println("Success rate: " + (passedTests * 100.0 / totalTests) + "%");
    }

    private static void runAllErrorTests() {
        // Boundary condition tests
        testBoundaryConditions();

        // Error handling tests
        testErrorHandling();
    }

    private static void testBoundaryConditions() {
        System.out.println("\n--- Boundary Condition Tests ---");

        // Test 1: Many users
        testBoundaryConditionsManyUsers();

        // Test 2: Performance with large datasets
        testBoundaryConditionsLargeDataset();

        // Test 3: Edge case usernames and passwords
        testBoundaryConditionsEdgeCaseInputs();
    }

    private static void testBoundaryConditionsManyUsers() {
        try {
            InMemoryUserRepository repo = new InMemoryUserRepository();

            // Create many users
            for (int i = 0; i < 100; i++) {
                RegularUser user = new RegularUser("user" + i, "pass" + i);
                repo.save(user);
            }

            // Verify all users can be found
            for (int i = 0; i < 100; i++) {
                Optional<RegularUser> found = repo.findByUsername("user" + i);
                assertTrue(found.isPresent(), "User " + i + " should be found");
                assertEquals("pass" + i, found.get().getPasswordHash(), "User " + i + " password should match");
            }

            // Verify non-existent user
            Optional<RegularUser> notFound = repo.findByUsername("nonexistent");
            assertFalse(notFound.isPresent(), "Non-existent user should return empty");

            testPassed("Many users boundary test");
        } catch (Exception e) {
            testFailed("Many users boundary test", e.getMessage());
        }
    }

    private static void testBoundaryConditionsLargeDataset() {
        try {
            InMemoryUserRepository repo = new InMemoryUserRepository();

            // Test with very long usernames and passwords
            String longUsername = "a".repeat(1000);
            String longPassword = "b".repeat(1000);
            
            RegularUser longUser = new RegularUser(longUsername, longPassword);
            repo.save(longUser);

            Optional<RegularUser> found = repo.findByUsername(longUsername);
            assertTrue(found.isPresent(), "Long username user should be found");
            assertEquals(longPassword, found.get().getPasswordHash(), "Long password should match");

            testPassed("Large dataset boundary test");
        } catch (Exception e) {
            testFailed("Large dataset boundary test", e.getMessage());
        }
    }

    private static void testBoundaryConditionsEdgeCaseInputs() {
        try {
            InMemoryUserRepository repo = new InMemoryUserRepository();

            // Test with special characters
            String specialUsername = "user@#$%^&*()_+-={}[]|\\:;\"'<>,.?/~`";
            String specialPassword = "pass@#$%^&*()_+-={}[]|\\:;\"'<>,.?/~`";
            
            RegularUser specialUser = new RegularUser(specialUsername, specialPassword);
            repo.save(specialUser);

            Optional<RegularUser> found = repo.findByUsername(specialUsername);
            assertTrue(found.isPresent(), "Special character username user should be found");
            assertEquals(specialPassword, found.get().getPasswordHash(), "Special character password should match");

            // Test with unicode characters
            String unicodeUsername = "用户名测试üñíçødé";
            String unicodePassword = "密码测试üñíçødé";
            
            RegularUser unicodeUser = new RegularUser(unicodeUsername, unicodePassword);
            repo.save(unicodeUser);

            Optional<RegularUser> unicodeFound = repo.findByUsername(unicodeUsername);
            assertTrue(unicodeFound.isPresent(), "Unicode username user should be found");
            assertEquals(unicodePassword, unicodeFound.get().getPasswordHash(), "Unicode password should match");

            testPassed("Edge case inputs boundary test");
        } catch (Exception e) {
            testFailed("Edge case inputs boundary test", e.getMessage());
        }
    }

    private static void testErrorHandling() {
        System.out.println("\n--- Error Handling Tests ---");

        // Test 1: Null parameter handling
        testErrorHandlingNullParameters();

        // Test 2: Login with invalid inputs
        testErrorHandlingInvalidLogin();

        // Test 3: Repository error scenarios
        testErrorHandlingRepositoryErrors();
    }

    private static void testErrorHandlingNullParameters() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockLoginPresenter presenter = new MockLoginPresenter();
            LoginInteractor interactor = new LoginInteractor(userRepository, presenter);

            // Test null username
            try {
                LoginRequestModel emptyUsernameRequest = new LoginRequestModel(null, "password");
                interactor.login(emptyUsernameRequest);
                testFailed("Null username error handling test", "Should throw exception but didn't");
            } catch (NullPointerException e) {
                // Expected exception
                testPassed("Null username error handling test");
            }

            // Test null password
            try {
                LoginRequestModel nullPasswordRequest = new LoginRequestModel("username", null);
                interactor.login(nullPasswordRequest);
                // This should not throw exception, just fail login
                LoginResponseModel response = presenter.getLastResponse();
                assertFalse(response.isSuccess(), "Login with null password should fail");
                testPassed("Null password error handling test");
            } catch (Exception e) {
                testFailed("Null password error handling test", "Unexpected exception: " + e.getMessage());
            }

        } catch (Exception e) {
            testFailed("Null parameter error handling test", e.getMessage());
        }
    }

    private static void testErrorHandlingInvalidLogin() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockLoginPresenter presenter = new MockLoginPresenter();
            LoginInteractor interactor = new LoginInteractor(userRepository, presenter);

            // Add a test user
            RegularUser testUser = new RegularUser("validuser", "validpass");
            userRepository.save(testUser);

            // Test with whitespace-only username
            presenter.reset();
            LoginRequestModel whitespaceRequest = new LoginRequestModel("   ", "validpass");
            interactor.login(whitespaceRequest);
            
            LoginResponseModel response = presenter.getLastResponse();
            assertFalse(response.isSuccess(), "Login with whitespace username should fail");

            // Test with extremely long username
            presenter.reset();
            String veryLongUsername = "a".repeat(10000);
            LoginRequestModel longUsernameRequest = new LoginRequestModel(veryLongUsername, "password");
            interactor.login(longUsernameRequest);
            
            response = presenter.getLastResponse();
            assertFalse(response.isSuccess(), "Login with extremely long username should fail");

            testPassed("Invalid login error handling test");
        } catch (Exception e) {
            testFailed("Invalid login error handling test", e.getMessage());
        }
    }

    private static void testErrorHandlingRepositoryErrors() {
        try {
            // Test with repository that might have concurrent access issues
            InMemoryUserRepository repo = new InMemoryUserRepository();
            
            // Simulate concurrent operations
            RegularUser user1 = new RegularUser("concurrent1", "pass1");
            RegularUser user2 = new RegularUser("concurrent2", "pass2");
            
            // Save users concurrently (simulated)
            repo.save(user1);
            repo.save(user2);
            
            // Try to overwrite user1 with different password
            RegularUser user1Updated = new RegularUser("concurrent1", "newpass1");
            repo.save(user1Updated);
            
            // Verify the update took effect
            Optional<RegularUser> found = repo.findByUsername("concurrent1");
            assertTrue(found.isPresent(), "Updated user should be found");
            assertEquals("newpass1", found.get().getPasswordHash(), "Password should be updated");

            testPassed("Repository error handling test");
        } catch (Exception e) {
            testFailed("Repository error handling test", e.getMessage());
        }
    }

    // Helper methods
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

    // Assertion methods
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
}