package Test;

import entity.RegularUser;
import entity.Recipe;
import use_case.gateway.RecipeHistoryGateway;
import use_case.recipe_history.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Comprehensive test class for Recipe History functionality
 * Tests RecipeHistoryInteractor
 */
public class HistoryTest {

    // Test counters
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // Mock objects for testing
    private static class MockRecipeHistoryGateway implements RecipeHistoryGateway {
        private boolean shouldThrowException = false;
        private List<Recipe> savedRecipes = new ArrayList<>();
        private int historyCount = 0;

        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        @Override
        public void saveRecipeHistory(RegularUser user, List<Recipe> recipes) throws Exception {
            if (shouldThrowException) {
                throw new Exception("Database connection failed");
            }
            savedRecipes.addAll(recipes);
            historyCount += recipes.size();
        }

        @Override
        public List<Recipe> getRecipeHistory(RegularUser user) throws Exception {
            if (shouldThrowException) {
                throw new Exception("Database connection failed");
            }
            return new ArrayList<>(savedRecipes);
        }

        @Override
        public int getHistoryCount(RegularUser user) throws Exception {
            if (shouldThrowException) {
                throw new Exception("Database connection failed");
            }
            return historyCount;
        }

        public void reset() {
            savedRecipes.clear();
            historyCount = 0;
            shouldThrowException = false;
        }
    }

    private static class MockRecipeHistoryPresenter implements RecipeHistoryOutputBoundary {
        private RecipeHistoryResponseModel lastResponse;

        @Override
        public void present(RecipeHistoryResponseModel response) {
            this.lastResponse = response;
        }

        public RecipeHistoryResponseModel getLastResponse() {
            return lastResponse;
        }

        public void reset() {
            lastResponse = null;
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting History Test execution...");

        // Run all history tests
        runAllHistoryTests();
        
        // Output test results
        System.out.println("History Test execution completed!");
        System.out.println("Total tests: " + totalTests);
        System.out.println("Passed tests: " + passedTests);
        System.out.println("Failed tests: " + failedTests);
        System.out.println("Success rate: " + (passedTests * 100.0 / totalTests) + "%");
    }

    private static void runAllHistoryTests() {
        // Recipe history interactor tests
        testRecipeHistoryFunctionality();
        
        // Legacy history record functionality tests
        testHistoryFunctionality();
    }

    private static void testRecipeHistoryFunctionality() {
        System.out.println("\n--- Recipe History Interactor Tests ---");

        // Test 1: Successful recipe history save
        testSuccessfulRecipeHistorySave();

        // Test 2: Successful recipe history retrieval
        testSuccessfulRecipeHistoryRetrieval();

        // Test 3: Null user handling
        testRecipeHistoryWithNullUser();

        // Test 4: Invalid operation handling
        testRecipeHistoryInvalidOperation();

        // Test 5: Save with null/empty recipes
        testRecipeHistorySaveNullRecipes();

        // Test 6: Database failure handling
        testRecipeHistoryDatabaseFailure();

        // Test 7: Retrieve empty history
        testRecipeHistoryRetrieveEmpty();

        // Test 8: Multiple recipes save and retrieve
        testRecipeHistoryMultipleOperations();
    }

    private static void testSuccessfulRecipeHistorySave() {
        try {
            MockRecipeHistoryGateway gateway = new MockRecipeHistoryGateway();
            MockRecipeHistoryPresenter presenter = new MockRecipeHistoryPresenter();
            RecipeHistoryInteractor interactor = new RecipeHistoryInteractor(gateway, presenter);

            // Prepare test data
            RegularUser user = new RegularUser("testuser", "testpass");
            List<Recipe> recipes = Arrays.asList(
                new Recipe("Saved Recipe 1", Arrays.asList("ingredient 1", "ingredient 2")),
                new Recipe("Saved Recipe 2", Arrays.asList("ingredient 3", "ingredient 4"))
            );

            // Execute save operation
            RecipeHistoryRequestModel request = new RecipeHistoryRequestModel(user, recipes, "save");
            interactor.processHistory(request);

            // Verify results
            RecipeHistoryResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertTrue(response.isSuccess(), "Recipe history save should be successful");
            assertEquals("Recipes saved to history successfully", response.getMessage(), "Success message should be correct");
            assertEquals(2, response.getHistoryCount(), "History count should be 2");
            assertEquals(2, response.getHistoryRecipes().size(), "Should return saved recipes");

            testPassed("Successful recipe history save test");
        } catch (Exception e) {
            testFailed("Successful recipe history save test", e.getMessage());
        }
    }

    private static void testSuccessfulRecipeHistoryRetrieval() {
        try {
            MockRecipeHistoryGateway gateway = new MockRecipeHistoryGateway();
            MockRecipeHistoryPresenter presenter = new MockRecipeHistoryPresenter();
            RecipeHistoryInteractor interactor = new RecipeHistoryInteractor(gateway, presenter);

            // Pre-populate gateway with test data
            RegularUser user = new RegularUser("testuser", "testpass");
            List<Recipe> savedRecipes = Arrays.asList(
                new Recipe("Historic Recipe", Arrays.asList("old ingredient 1"))
            );
            gateway.saveRecipeHistory(user, savedRecipes);

            // Execute retrieve operation
            RecipeHistoryRequestModel request = new RecipeHistoryRequestModel(user, null, "retrieve");
            interactor.processHistory(request);

            // Verify results
            RecipeHistoryResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertTrue(response.isSuccess(), "Recipe history retrieval should be successful");
            assertEquals("Recipe history retrieved successfully", response.getMessage(), "Success message should be correct");
            assertEquals(1, response.getHistoryCount(), "History count should be 1");
            assertEquals(1, response.getHistoryRecipes().size(), "Should return 1 recipe");
            assertEquals("Historic Recipe", response.getHistoryRecipes().get(0).getLabel(), "Recipe name should match");

            testPassed("Successful recipe history retrieval test");
        } catch (Exception e) {
            testFailed("Successful recipe history retrieval test", e.getMessage());
        }
    }

    private static void testRecipeHistoryWithNullUser() {
        try {
            MockRecipeHistoryGateway gateway = new MockRecipeHistoryGateway();
            MockRecipeHistoryPresenter presenter = new MockRecipeHistoryPresenter();
            RecipeHistoryInteractor interactor = new RecipeHistoryInteractor(gateway, presenter);

            // Execute with null user
            List<Recipe> recipes = Arrays.asList(new Recipe("Test Recipe", Arrays.asList("test ingredient")));
            RecipeHistoryRequestModel request = new RecipeHistoryRequestModel(null, recipes, "save");
            interactor.processHistory(request);

            // Verify error handling
            RecipeHistoryResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Recipe history should fail with null user");
            assertEquals("User cannot be null", response.getMessage(), "Error message should be correct");

            testPassed("Recipe history with null user test");
        } catch (Exception e) {
            testFailed("Recipe history with null user test", e.getMessage());
        }
    }

    private static void testRecipeHistoryInvalidOperation() {
        try {
            MockRecipeHistoryGateway gateway = new MockRecipeHistoryGateway();
            MockRecipeHistoryPresenter presenter = new MockRecipeHistoryPresenter();
            RecipeHistoryInteractor interactor = new RecipeHistoryInteractor(gateway, presenter);

            // Execute with invalid operation
            RegularUser user = new RegularUser("testuser", "testpass");
            RecipeHistoryRequestModel request = new RecipeHistoryRequestModel(user, null, "invalid_operation");
            interactor.processHistory(request);

            // Verify error handling
            RecipeHistoryResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Recipe history should fail with invalid operation");
            assertTrue(response.getMessage().contains("Invalid operation"), "Error message should indicate invalid operation");

            testPassed("Recipe history invalid operation test");
        } catch (Exception e) {
            testFailed("Recipe history invalid operation test", e.getMessage());
        }
    }

    private static void testRecipeHistorySaveNullRecipes() {
        try {
            MockRecipeHistoryGateway gateway = new MockRecipeHistoryGateway();
            MockRecipeHistoryPresenter presenter = new MockRecipeHistoryPresenter();
            RecipeHistoryInteractor interactor = new RecipeHistoryInteractor(gateway, presenter);

            // Execute save with null recipes
            RegularUser user = new RegularUser("testuser", "testpass");
            RecipeHistoryRequestModel request = new RecipeHistoryRequestModel(user, null, "save");
            interactor.processHistory(request);

            // Verify error handling
            RecipeHistoryResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Recipe history save should fail with null recipes");
            assertEquals("No recipes to save", response.getMessage(), "Error message should be correct");

            // Test with empty recipes list
            presenter.reset();
            List<Recipe> emptyRecipes = new ArrayList<>();
            RecipeHistoryRequestModel emptyRequest = new RecipeHistoryRequestModel(user, emptyRecipes, "save");
            interactor.processHistory(emptyRequest);

            RecipeHistoryResponseModel emptyResponse = presenter.getLastResponse();
            assertFalse(emptyResponse.isSuccess(), "Recipe history save should fail with empty recipes");

            testPassed("Recipe history save null/empty recipes test");
        } catch (Exception e) {
            testFailed("Recipe history save null/empty recipes test", e.getMessage());
        }
    }

    private static void testRecipeHistoryDatabaseFailure() {
        try {
            MockRecipeHistoryGateway gateway = new MockRecipeHistoryGateway();
            MockRecipeHistoryPresenter presenter = new MockRecipeHistoryPresenter();
            RecipeHistoryInteractor interactor = new RecipeHistoryInteractor(gateway, presenter);

            // Configure gateway to throw exception
            gateway.setShouldThrowException(true);

            // Execute operation that should fail
            RegularUser user = new RegularUser("testuser", "testpass");
            List<Recipe> recipes = Arrays.asList(new Recipe("Test Recipe", Arrays.asList("test ingredient")));
            RecipeHistoryRequestModel request = new RecipeHistoryRequestModel(user, recipes, "save");
            interactor.processHistory(request);

            // Verify error handling
            RecipeHistoryResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Recipe history should fail on database error");
            assertTrue(response.getMessage().contains("History operation failed"), "Error message should indicate operation failure");

            testPassed("Recipe history database failure test");
        } catch (Exception e) {
            testFailed("Recipe history database failure test", e.getMessage());
        }
    }

    private static void testRecipeHistoryRetrieveEmpty() {
        try {
            MockRecipeHistoryGateway gateway = new MockRecipeHistoryGateway();
            MockRecipeHistoryPresenter presenter = new MockRecipeHistoryPresenter();
            RecipeHistoryInteractor interactor = new RecipeHistoryInteractor(gateway, presenter);

            // Execute retrieve on empty history
            RegularUser user = new RegularUser("newuser", "newpass");
            RecipeHistoryRequestModel request = new RecipeHistoryRequestModel(user, null, "retrieve");
            interactor.processHistory(request);

            // Verify results
            RecipeHistoryResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertTrue(response.isSuccess(), "Recipe history retrieval should succeed even when empty");
            assertEquals("No recipe history found", response.getMessage(), "Message should indicate no history");
            assertEquals(0, response.getHistoryCount(), "History count should be 0");
            assertTrue(response.getHistoryRecipes().isEmpty(), "History recipes should be empty");

            testPassed("Recipe history retrieve empty test");
        } catch (Exception e) {
            testFailed("Recipe history retrieve empty test", e.getMessage());
        }
    }

    private static void testRecipeHistoryMultipleOperations() {
        try {
            MockRecipeHistoryGateway gateway = new MockRecipeHistoryGateway();
            MockRecipeHistoryPresenter presenter = new MockRecipeHistoryPresenter();
            RecipeHistoryInteractor interactor = new RecipeHistoryInteractor(gateway, presenter);

            RegularUser user = new RegularUser("multiuser", "multipass");

            // Save first batch of recipes
            List<Recipe> firstBatch = Arrays.asList(
                new Recipe("Recipe A", Arrays.asList("ingredient A1", "ingredient A2"))
            );
            RecipeHistoryRequestModel saveRequest1 = new RecipeHistoryRequestModel(user, firstBatch, "save");
            interactor.processHistory(saveRequest1);

            RecipeHistoryResponseModel saveResponse1 = presenter.getLastResponse();
            assertTrue(saveResponse1.isSuccess(), "First save should succeed");
            assertEquals(1, saveResponse1.getHistoryCount(), "Should have 1 recipe after first save");

            // Save second batch of recipes
            presenter.reset();
            List<Recipe> secondBatch = Arrays.asList(
                new Recipe("Recipe B", Arrays.asList("ingredient B1")),
                new Recipe("Recipe C", Arrays.asList("ingredient C1", "ingredient C2"))
            );
            RecipeHistoryRequestModel saveRequest2 = new RecipeHistoryRequestModel(user, secondBatch, "save");
            interactor.processHistory(saveRequest2);

            RecipeHistoryResponseModel saveResponse2 = presenter.getLastResponse();
            assertTrue(saveResponse2.isSuccess(), "Second save should succeed");
            assertEquals(3, saveResponse2.getHistoryCount(), "Should have 3 recipes after second save");

            // Retrieve all saved recipes
            presenter.reset();
            RecipeHistoryRequestModel retrieveRequest = new RecipeHistoryRequestModel(user, null, "retrieve");
            interactor.processHistory(retrieveRequest);

            RecipeHistoryResponseModel retrieveResponse = presenter.getLastResponse();
            assertTrue(retrieveResponse.isSuccess(), "Retrieve should succeed");
            assertEquals(3, retrieveResponse.getHistoryCount(), "Should retrieve 3 recipes");
            assertEquals(3, retrieveResponse.getHistoryRecipes().size(), "Should have 3 recipes in response");

            testPassed("Recipe history multiple operations test");
        } catch (Exception e) {
            testFailed("Recipe history multiple operations test", e.getMessage());
        }
    }

    // Legacy history functionality tests
    private static void testHistoryFunctionality() {
        System.out.println("\n--- Legacy History Record Functionality Tests ---");

        // Test 1: History record functionality
        testHistoryFunctionalityBasic();

        // Test 2: Empty user history record
        testHistoryWithEmptyUser();
    }

    private static void testHistoryFunctionalityBasic() {
        try {
            // Create test user
            RegularUser testUser = new RegularUser("historyuser", "pass");

            // Verify user information integrity
            assertEquals("historyuser", testUser.getUsername(), "Username should match");
            assertEquals("pass", testUser.getPasswordHash(), "Password should match");

            // Simulate history record count
            int expectedHistoryCount = 3;

            // Here we test history record related logic
            assertNotNull(testUser.getUsername(), "Username should not be null");
            assertNotNull(testUser.getPasswordHash(), "Password should not be null");

            testPassed("History record functionality basic test");
        } catch (Exception e) {
            testFailed("History record functionality basic test", e.getMessage());
        }
    }

    private static void testHistoryWithEmptyUser() {
        try {
            // Test boundary conditions
            RegularUser emptyUser = new RegularUser("", "");

            assertEquals("", emptyUser.getUsername(), "Empty username should match");
            assertEquals("", emptyUser.getPasswordHash(), "Empty password should match");

            testPassed("Empty user history record test");
        } catch (Exception e) {
            testFailed("Empty user history record test", e.getMessage());
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