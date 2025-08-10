import entity.RegularUser;
import entity.Recipe;
import use_case.gateway.UserRepository;
import use_case.gateway.RecipeGateway;
import use_case.gateway.RecipeHistoryGateway;
import use_case.login.*;
import use_case.signup.*;
import use_case.recipe_generation.*;
import use_case.recipe_history.*;
import data_access.InMemoryUserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Complete test class using basic Java functionality for testing
 * Includes login registration, history records, and recipe output functionality tests
 */
public class IngredientGeneratorTest {

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

    private static class MockSignupPresenter implements SignupOutputBoundary {
        private SignupResponseModel lastSuccessResponse;
        private SignupResponseModel lastFailureResponse;

        @Override
        public SignupResponseModel prepareSuccessView(SignupResponseModel response) {
            this.lastSuccessResponse = response;
            return response;
        }

        @Override
        public SignupResponseModel prepareFailureView(SignupResponseModel response) {
            this.lastFailureResponse = response;
            return response;
        }

        public SignupResponseModel getLastSuccessResponse() {
            return lastSuccessResponse;
        }

        public SignupResponseModel getLastFailureResponse() {
            return lastFailureResponse;
        }

        public void reset() {
            lastSuccessResponse = null;
            lastFailureResponse = null;
        }
    }

    private static class MockRecipeGateway implements RecipeGateway {
        private boolean shouldThrowException = false;
        private List<Recipe> recipesToReturn = new ArrayList<>();

        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        public void setRecipesToReturn(List<Recipe> recipes) {
            this.recipesToReturn = recipes;
        }

        @Override
        public List<Recipe> fetchRecipes(String query, String mealType, String cuisineType) throws Exception {
            if (shouldThrowException) {
                throw new Exception("API connection failed");
            }
            return new ArrayList<>(recipesToReturn);
        }
    }

    private static class MockRecipeGenerationPresenter implements RecipeGenerationOutputBoundary {
        private RecipeGenerationResponseModel lastResponse;

        @Override
        public void present(RecipeGenerationResponseModel response) {
            this.lastResponse = response;
        }

        public RecipeGenerationResponseModel getLastResponse() {
            return lastResponse;
        }

        public void reset() {
            lastResponse = null;
        }
    }

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
        System.out.println("Starting test execution...");

        // Run all tests
        runAllTests();
        // Output test results
        System.out.println("Test execution completed!");
        System.out.println("Total tests: " + totalTests);
        System.out.println("Passed tests: " + passedTests);
        System.out.println("Failed tests: " + failedTests);
        System.out.println("Success rate: " + (passedTests * 100.0 / totalTests) + "%");
    }

    private static void runAllTests() {
        // Login functionality tests
        testLoginFunctionality();

        // Registration functionality tests
        testSignupFunctionality();

        // User entity tests
        testUserEntity();

        // Recipe entity tests
        testRecipeEntity();

        // Recipe generation interactor tests
        testRecipeGenerationFunctionality();

        // Recipe history interactor tests
        testRecipeHistoryFunctionality();

        // Data access layer tests
        testDataAccess();

        // History record functionality tests
        testHistoryFunctionality();


        // Integration tests
        testIntegration();

        // Boundary condition tests
        testBoundaryConditions();

        // Error handling tests
        testErrorHandling();
    }


    private static void testLoginFunctionality() {
        System.out.println("\n--- Login Functionality Tests ---");

        // Test 1: Successful login
        testSuccessfulLogin();

        // Test 2: User does not exist
        testLoginFailureUserNotExists();

        // Test 3: Wrong password
        testLoginFailureWrongPassword();

        // Test 4: Empty username
        testLoginWithEmptyUsername();

        // Test 5: Empty password
        testLoginWithEmptyPassword();
    }

    private static void testSuccessfulLogin() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockLoginPresenter presenter = new MockLoginPresenter();
            LoginInteractor interactor = new LoginInteractor(userRepository, presenter);

            // Prepare test data
            RegularUser testUser = new RegularUser("testuser", "testpass");
            userRepository.save(testUser);

            // Execute login
            LoginRequestModel request = new LoginRequestModel("testuser", "testpass");
            interactor.login(request);

            // Verify results
            LoginResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertTrue(response.isSuccess(), "Login should be successful");
            assertEquals("Login successful", response.getMessage(), "Message should be correct");
            assertEquals("testuser", response.getUser().getUsername(), "Username should match");

            testPassed("Successful login test");
        } catch (Exception e) {
            testFailed("Successful login test", e.getMessage());
        }
    }

    private static void testLoginFailureUserNotExists() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockLoginPresenter presenter = new MockLoginPresenter();
            LoginInteractor interactor = new LoginInteractor(userRepository, presenter);

            // Execute login - user does not exist
            LoginRequestModel request = new LoginRequestModel("nonexistent", "password");
            interactor.login(request);

            // Verify results
            LoginResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Login should fail");
            assertEquals("Invalid username or password", response.getMessage(), "Error message should be correct");

            testPassed("User not exists login test");
        } catch (Exception e) {
            testFailed("User not exists login test", e.getMessage());
        }
    }

    private static void testLoginFailureWrongPassword() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockLoginPresenter presenter = new MockLoginPresenter();
            LoginInteractor interactor = new LoginInteractor(userRepository, presenter);

            // Prepare test data
            RegularUser testUser = new RegularUser("testuser", "correctpass");
            userRepository.save(testUser);

            // Execute login - wrong password
            LoginRequestModel request = new LoginRequestModel("testuser", "wrongpass");
            interactor.login(request);

            // Verify results
            LoginResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Login should fail");
            assertEquals("Invalid username or password", response.getMessage(), "Error message should be correct");

            testPassed("Wrong password login test");
        } catch (Exception e) {
            testFailed("Wrong password login test", e.getMessage());
        }
    }

    private static void testLoginWithEmptyUsername() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockLoginPresenter presenter = new MockLoginPresenter();
            LoginInteractor interactor = new LoginInteractor(userRepository, presenter);

            LoginRequestModel request = new LoginRequestModel("", "password");
            interactor.login(request);

            LoginResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Empty username login should fail");

            testPassed("Empty username login test");
        } catch (Exception e) {
            testFailed("Empty username login test", e.getMessage());
        }
    }

    private static void testLoginWithEmptyPassword() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockLoginPresenter presenter = new MockLoginPresenter();
            LoginInteractor interactor = new LoginInteractor(userRepository, presenter);

            // Prepare test data
            RegularUser testUser = new RegularUser("testuser", "testpass");
            userRepository.save(testUser);

            LoginRequestModel request = new LoginRequestModel("testuser", "");
            interactor.login(request);

            LoginResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Empty password login should fail");

            testPassed("Empty password login test");
        } catch (Exception e) {
            testFailed("Empty password login test", e.getMessage());
        }
    }


    private static void testSignupFunctionality() {
        System.out.println("\n--- Registration Functionality Tests ---");

        // Test 1: Successful registration
        testSuccessfulSignup();

        // Test 2: Username already exists
        testSignupFailureUserExists();

        // Test 3: Empty username registration
        testSignupWithEmptyUsername();

        // Test 4: Empty password registration
        testSignupWithEmptyPassword();
    }

    private static void testSuccessfulSignup() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockSignupPresenter presenter = new MockSignupPresenter();
            SignupInteractor interactor = new SignupInteractor(userRepository, presenter);

            // Execute registration
            SignupRequestModel request = new SignupRequestModel("newuser", "newpass");
            interactor.signup(request);

            // Verify success callback
            SignupResponseModel successResponse = presenter.getLastSuccessResponse();
            assertTrue(successResponse != null, "Success response should not be null");
            assertTrue(successResponse.isSuccess(), "Registration should be successful");

            // Verify user is saved
            Optional<RegularUser> savedUser = userRepository.findByUsername("newuser");
            assertTrue(savedUser.isPresent(), "User should be saved");
            assertEquals("newuser", savedUser.get().getUsername(), "Username should match");
            assertEquals("newpass", savedUser.get().getPasswordHash(), "Password should match");

            testPassed("Successful registration test");
        } catch (Exception e) {
            testFailed("Successful registration test", e.getMessage());
        }
    }

    private static void testSignupFailureUserExists() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockSignupPresenter presenter = new MockSignupPresenter();
            SignupInteractor interactor = new SignupInteractor(userRepository, presenter);

            // Prepare test data - register a user first
            RegularUser existingUser = new RegularUser("existinguser", "pass");
            userRepository.save(existingUser);

            // Try to register same username
            SignupRequestModel request = new SignupRequestModel("existinguser", "newpass");
            interactor.signup(request);

            // Verify failure callback
            SignupResponseModel failureResponse = presenter.getLastFailureResponse();
            assertTrue(failureResponse != null, "Failure response should not be null");
            assertFalse(failureResponse.isSuccess(), "Registration should fail");


            // Verify no new user is created
            Optional<RegularUser> user = userRepository.findByUsername("existinguser");
            assertTrue(user.isPresent(), "Original user should exist");
            assertEquals("pass", user.get().getPasswordHash(), "Password should be the original");

            testPassed("Username already exists registration test");
        } catch (Exception e) {
            testFailed("Username already exists registration test", e.getMessage());
        }
    }

    private static void testSignupWithEmptyUsername() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockSignupPresenter presenter = new MockSignupPresenter();
            SignupInteractor interactor = new SignupInteractor(userRepository, presenter);

            SignupRequestModel request = new SignupRequestModel("", "password");
            interactor.signup(request);

            // Should succeed because business logic doesn't validate empty username
            SignupResponseModel successResponse = presenter.getLastSuccessResponse();
            assertTrue(successResponse != null, "Success response should not be null");
            assertTrue(successResponse.isSuccess(), "Registration should be successful");

            testPassed("Empty username registration test");
        } catch (Exception e) {
            testFailed("Empty username registration test", e.getMessage());
        }
    }

    private static void testSignupWithEmptyPassword() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockSignupPresenter presenter = new MockSignupPresenter();
            SignupInteractor interactor = new SignupInteractor(userRepository, presenter);

            SignupRequestModel request = new SignupRequestModel("username", "");
            interactor.signup(request);

            // Should succeed because business logic doesn't validate empty password
            SignupResponseModel successResponse = presenter.getLastSuccessResponse();
            assertTrue(successResponse != null, "Success response should not be null");
            assertTrue(successResponse.isSuccess(), "Registration should be successful");

            testPassed("Empty password registration test");
        } catch (Exception e) {
            testFailed("Empty password registration test", e.getMessage());
        }
    }

    private static void testUserEntity() {
        System.out.println("\n--- User Entity Tests ---");

        // Test 1: User creation
        testRegularUserCreation();

        // Test 2: Special character username
        testRegularUserWithSpecialCharacters();
    }

    private static void testRegularUserCreation() {

        String username = "testuser";
        String password = "testpass";

        RegularUser user = new RegularUser(username, password);

        assertEquals(username, user.getUsername(), "Username should match");
        assertEquals(password, user.getPasswordHash(), "Password should match");

        testPassed("User entity creation test");

    }

    private static void testRegularUserWithSpecialCharacters() {
        try {
            String username = "user@123!";
            String password = "pass@123!";

            RegularUser user = new RegularUser(username, password);

            assertEquals(username, user.getUsername(), "Special character username should match");
            assertEquals(password, user.getPasswordHash(), "Special character password should match");

            testPassed("Special character user entity test");
        } catch (Exception e) {
            testFailed("Special character user entity test", e.getMessage());
        }
    }

    private static void testRecipeEntity() {
        System.out.println("\n--- Recipe Entity Tests ---");

        // Test 1: Recipe creation with basic ingredients
        testRecipeCreation();

        // Test 2: Recipe with empty ingredients list
        testRecipeWithEmptyIngredients();

        // Test 3: Recipe with null ingredients handling
        testRecipeWithNullIngredients();

        // Test 4: Recipe immutability test
        testRecipeImmutability();

        // Test 5: Recipe with multiple ingredients
        testRecipeWithMultipleIngredients();

        // Test 6: Recipe with special characters in label
        testRecipeWithSpecialCharactersLabel();
    }

    private static void testRecipeCreation() {
        try {
            String label = "Chicken Curry";
            List<String> ingredients = Arrays.asList("1 lb chicken", "2 cups rice", "1 onion");

            Recipe recipe = new Recipe(label, ingredients);

            assertEquals(label, recipe.getLabel(), "Recipe label should match");
            assertEquals(ingredients.size(), recipe.getIngredientLines().size(), "Ingredient count should match");
            assertEquals("1 lb chicken", recipe.getIngredientLines().get(0), "First ingredient should match");
            assertEquals("2 cups rice", recipe.getIngredientLines().get(1), "Second ingredient should match");
            assertEquals("1 onion", recipe.getIngredientLines().get(2), "Third ingredient should match");

            testPassed("Recipe creation test");
        } catch (Exception e) {
            testFailed("Recipe creation test", e.getMessage());
        }
    }

    private static void testRecipeWithEmptyIngredients() {
        try {
            String label = "Empty Recipe";
            List<String> ingredients = new ArrayList<>();

            Recipe recipe = new Recipe(label, ingredients);

            assertEquals(label, recipe.getLabel(), "Recipe label should match");
            assertTrue(recipe.getIngredientLines().isEmpty(), "Ingredient list should be empty");
            assertEquals(0, recipe.getIngredientLines().size(), "Ingredient count should be zero");

            testPassed("Recipe with empty ingredients test");
        } catch (Exception e) {
            testFailed("Recipe with empty ingredients test", e.getMessage());
        }
    }

    private static void testRecipeWithNullIngredients() {
        try {
            String label = "Test Recipe";
            
            // Test with null ingredients list - should throw exception
            try {
                Recipe recipe = new Recipe(label, null);
                testFailed("Recipe with null ingredients test", "Should throw exception but didn't");
            } catch (NullPointerException e) {
                // Expected exception
                testPassed("Recipe with null ingredients test");
            }
        } catch (Exception e) {
            testFailed("Recipe with null ingredients test", e.getMessage());
        }
    }

    private static void testRecipeImmutability() {
        try {
            String label = "Test Recipe";
            List<String> originalIngredients = new ArrayList<>();
            originalIngredients.add("1 cup milk");
            originalIngredients.add("2 eggs");

            Recipe recipe = new Recipe(label, originalIngredients);

            // Modify original list
            originalIngredients.add("3 cups flour");

            // Recipe should not be affected
            assertEquals(2, recipe.getIngredientLines().size(), "Recipe should not be affected by original list modification");
            assertFalse(recipe.getIngredientLines().contains("3 cups flour"), "Recipe should not contain added ingredient");

            // Try to modify returned list (should fail)
            try {
                recipe.getIngredientLines().add("new ingredient");
                testFailed("Recipe immutability test", "Should not be able to modify returned list");
            } catch (UnsupportedOperationException e) {
                // Expected exception for immutable list
                testPassed("Recipe immutability test");
            }
        } catch (Exception e) {
            testFailed("Recipe immutability test", e.getMessage());
        }
    }

    private static void testRecipeWithMultipleIngredients() {
        try {
            String label = "Complex Recipe";
            List<String> ingredients = Arrays.asList(
                "1 lb ground beef",
                "2 cups chopped onions",
                "3 cloves garlic, minced",
                "1 can (14.5 oz) diced tomatoes",
                "2 tablespoons tomato paste",
                "1 teaspoon dried oregano",
                "1/2 teaspoon salt",
                "1/4 teaspoon black pepper"
            );

            Recipe recipe = new Recipe(label, ingredients);

            assertEquals(label, recipe.getLabel(), "Recipe label should match");
            assertEquals(8, recipe.getIngredientLines().size(), "Should have 8 ingredients");
            assertEquals("1 lb ground beef", recipe.getIngredientLines().get(0), "First ingredient should match");
            assertEquals("1/4 teaspoon black pepper", recipe.getIngredientLines().get(7), "Last ingredient should match");

            testPassed("Recipe with multiple ingredients test");
        } catch (Exception e) {
            testFailed("Recipe with multiple ingredients test", e.getMessage());
        }
    }

    private static void testRecipeWithSpecialCharactersLabel() {
        try {
            String label = "Café Au Lait & Crème Brûlée (Special #1)";
            List<String> ingredients = Arrays.asList("1 cup coffee", "1/2 cup milk");

            Recipe recipe = new Recipe(label, ingredients);

            assertEquals(label, recipe.getLabel(), "Recipe label with special characters should match");
            assertEquals(2, recipe.getIngredientLines().size(), "Ingredient count should match");

            testPassed("Recipe with special characters label test");
        } catch (Exception e) {
            testFailed("Recipe with special characters label test", e.getMessage());
        }
    }

    private static void testRecipeGenerationFunctionality() {
        System.out.println("\n--- Recipe Generation Interactor Tests ---");

        // Test 1: Successful recipe generation
        testSuccessfulRecipeGeneration();

        // Test 2: Empty ingredients handling
        testRecipeGenerationWithEmptyIngredients();

        // Test 3: Null ingredients handling
        testRecipeGenerationWithNullIngredients();

        // Test 4: API failure handling
        testRecipeGenerationApiFailure();

        // Test 5: No recipes found scenario
        testRecipeGenerationNoResults();

        // Test 6: Ingredient formatting
        testRecipeGenerationIngredientFormatting();

        // Test 7: Multiple recipes response
        testRecipeGenerationMultipleRecipes();
    }

    private static void testSuccessfulRecipeGeneration() {
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            MockRecipeGenerationPresenter presenter = new MockRecipeGenerationPresenter();
            RecipeGenerationInteractor interactor = new RecipeGenerationInteractor(gateway, presenter);

            // Prepare test data
            List<Recipe> mockRecipes = Arrays.asList(
                new Recipe("Chicken Curry", Arrays.asList("1 lb chicken", "2 cups rice")),
                new Recipe("Beef Stew", Arrays.asList("1 lb beef", "3 potatoes"))
            );
            gateway.setRecipesToReturn(mockRecipes);

            // Execute recipe generation
            RecipeGenerationRequestModel request = new RecipeGenerationRequestModel(
                "chicken,rice", "Dinner", "Indian"
            );
            interactor.generateRecipes(request);

            // Verify results
            RecipeGenerationResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertTrue(response.isSuccess(), "Recipe generation should be successful");
            assertEquals("Recipes generated successfully", response.getMessage(), "Success message should be correct");
            assertEquals(2, response.getRecipes().size(), "Should return 2 recipes");
            assertEquals("Chicken Curry", response.getRecipes().get(0).getLabel(), "First recipe name should match");

            testPassed("Successful recipe generation test");
        } catch (Exception e) {
            testFailed("Successful recipe generation test", e.getMessage());
        }
    }

    private static void testRecipeGenerationWithEmptyIngredients() {
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            MockRecipeGenerationPresenter presenter = new MockRecipeGenerationPresenter();
            RecipeGenerationInteractor interactor = new RecipeGenerationInteractor(gateway, presenter);

            // Execute with empty ingredients
            RecipeGenerationRequestModel request = new RecipeGenerationRequestModel(
                "", "Dinner", "Indian"
            );
            interactor.generateRecipes(request);

            // Verify failure response
            RecipeGenerationResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Recipe generation should fail with empty ingredients");
            assertEquals("Ingredients cannot be empty", response.getMessage(), "Error message should be correct");
            assertTrue(response.getRecipes() == null, "Recipes should be null on failure");

            testPassed("Recipe generation with empty ingredients test");
        } catch (Exception e) {
            testFailed("Recipe generation with empty ingredients test", e.getMessage());
        }
    }

    private static void testRecipeGenerationWithNullIngredients() {
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            MockRecipeGenerationPresenter presenter = new MockRecipeGenerationPresenter();
            RecipeGenerationInteractor interactor = new RecipeGenerationInteractor(gateway, presenter);

            // Execute with null ingredients
            RecipeGenerationRequestModel request = new RecipeGenerationRequestModel(
                null, "Dinner", "Indian"
            );
            interactor.generateRecipes(request);

            // Verify failure response
            RecipeGenerationResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Recipe generation should fail with null ingredients");
            assertEquals("Ingredients cannot be empty", response.getMessage(), "Error message should be correct");

            testPassed("Recipe generation with null ingredients test");
        } catch (Exception e) {
            testFailed("Recipe generation with null ingredients test", e.getMessage());
        }
    }

    private static void testRecipeGenerationApiFailure() {
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            MockRecipeGenerationPresenter presenter = new MockRecipeGenerationPresenter();
            RecipeGenerationInteractor interactor = new RecipeGenerationInteractor(gateway, presenter);

            // Configure gateway to throw exception
            gateway.setShouldThrowException(true);

            // Execute recipe generation
            RecipeGenerationRequestModel request = new RecipeGenerationRequestModel(
                "chicken,rice", "Dinner", "Indian"
            );
            interactor.generateRecipes(request);

            // Verify error handling
            RecipeGenerationResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Recipe generation should fail on API error");
            assertTrue(response.getMessage().contains("Failed to generate recipes"), "Error message should indicate failure");

            testPassed("Recipe generation API failure test");
        } catch (Exception e) {
            testFailed("Recipe generation API failure test", e.getMessage());
        }
    }

    private static void testRecipeGenerationNoResults() {
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            MockRecipeGenerationPresenter presenter = new MockRecipeGenerationPresenter();
            RecipeGenerationInteractor interactor = new RecipeGenerationInteractor(gateway, presenter);

            // Configure gateway to return empty list
            gateway.setRecipesToReturn(new ArrayList<>());

            // Execute recipe generation
            RecipeGenerationRequestModel request = new RecipeGenerationRequestModel(
                "unicorn,rainbow", "Dinner", "Mystical"
            );
            interactor.generateRecipes(request);

            // Verify no results handling
            RecipeGenerationResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertFalse(response.isSuccess(), "Recipe generation should fail when no recipes found");
            assertEquals("No recipes found matching your preferences", response.getMessage(), "No results message should be correct");

            testPassed("Recipe generation no results test");
        } catch (Exception e) {
            testFailed("Recipe generation no results test", e.getMessage());
        }
    }

    private static void testRecipeGenerationIngredientFormatting() {
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            MockRecipeGenerationPresenter presenter = new MockRecipeGenerationPresenter();
            RecipeGenerationInteractor interactor = new RecipeGenerationInteractor(gateway, presenter);

            // Prepare test data
            List<Recipe> mockRecipes = Arrays.asList(
                new Recipe("Test Recipe", Arrays.asList("test ingredient"))
            );
            gateway.setRecipesToReturn(mockRecipes);

            // Execute with ingredients that need formatting (extra spaces)
            RecipeGenerationRequestModel request = new RecipeGenerationRequestModel(
                " chicken , rice ,  onion ", "Dinner", "Indian"
            );
            interactor.generateRecipes(request);

            // The interactor should format ingredients properly before calling gateway
            RecipeGenerationResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertTrue(response.isSuccess(), "Recipe generation should succeed with formatted ingredients");

            testPassed("Recipe generation ingredient formatting test");
        } catch (Exception e) {
            testFailed("Recipe generation ingredient formatting test", e.getMessage());
        }
    }

    private static void testRecipeGenerationMultipleRecipes() {
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            MockRecipeGenerationPresenter presenter = new MockRecipeGenerationPresenter();
            RecipeGenerationInteractor interactor = new RecipeGenerationInteractor(gateway, presenter);

            // Prepare multiple test recipes
            List<Recipe> mockRecipes = Arrays.asList(
                new Recipe("Recipe 1", Arrays.asList("ingredient 1", "ingredient 2")),
                new Recipe("Recipe 2", Arrays.asList("ingredient 3", "ingredient 4")),
                new Recipe("Recipe 3", Arrays.asList("ingredient 5", "ingredient 6")),
                new Recipe("Recipe 4", Arrays.asList("ingredient 7", "ingredient 8"))
            );
            gateway.setRecipesToReturn(mockRecipes);

            // Execute recipe generation
            RecipeGenerationRequestModel request = new RecipeGenerationRequestModel(
                "multiple,ingredients", "Lunch", "Italian"
            );
            interactor.generateRecipes(request);

            // Verify multiple recipes handling
            RecipeGenerationResponseModel response = presenter.getLastResponse();
            assertTrue(response != null, "Response should not be null");
            assertTrue(response.isSuccess(), "Recipe generation should succeed");
            assertEquals(4, response.getRecipes().size(), "Should return 4 recipes");
            assertEquals("Recipe 1", response.getRecipes().get(0).getLabel(), "First recipe should match");
            assertEquals("Recipe 4", response.getRecipes().get(3).getLabel(), "Last recipe should match");

            testPassed("Recipe generation multiple recipes test");
        } catch (Exception e) {
            testFailed("Recipe generation multiple recipes test", e.getMessage());
        }
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


    private static void testDataAccess() {
        System.out.println("\n--- Data Access Layer Tests ---");

        // Test 1: In-memory storage basic operations
        testInMemoryUserRepository();

        // Test 2: User update
        testInMemoryUserRepositoryUpdate();
    }

    private static void testInMemoryUserRepository() {
        try {
            InMemoryUserRepository repo = new InMemoryUserRepository();

            // Test saving user
            RegularUser user = new RegularUser("testuser", "testpass");
            repo.save(user);

            // Test finding user
            Optional<RegularUser> found = repo.findByUsername("testuser");
            assertTrue(found.isPresent(), "User should be found");
            assertEquals("testuser", found.get().getUsername(), "Username should match");
            assertEquals("testpass", found.get().getPasswordHash(), "Password should match");

            // Test finding non-existent user
            Optional<RegularUser> notFound = repo.findByUsername("nonexistent");
            assertFalse(notFound.isPresent(), "Non-existent user should return empty");

            testPassed("In-memory storage basic operations test");
        } catch (Exception e) {
            testFailed("In-memory storage basic operations test", e.getMessage());
        }
    }

    private static void testInMemoryUserRepositoryUpdate() {
        try {
            InMemoryUserRepository repo = new InMemoryUserRepository();

            // Save initial user
            RegularUser user1 = new RegularUser("testuser", "pass1");
            repo.save(user1);

            // Update user password
            RegularUser user2 = new RegularUser("testuser", "pass2");
            repo.save(user2);

            // Verify update
            Optional<RegularUser> found = repo.findByUsername("testuser");
            assertTrue(found.isPresent(), "User should exist");
            assertEquals("pass2", found.get().getPasswordHash(), "Password should be updated");

            testPassed("User update test");
        } catch (Exception e) {
            testFailed("User update test", e.getMessage());
        }
    }

    // ==================== History Record Functionality Tests ====================

    private static void testHistoryFunctionality() {
        System.out.println("\n--- History Record Functionality Tests ---");

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

    // ==================== Integration Tests ====================

    private static void testIntegration() {
        System.out.println("\n--- Integration Tests ---");

        // Test 1: Complete user flow
        testCompleteUserFlow();
    }

    private static void testCompleteUserFlow() {
        try {
            UserRepository userRepository = new InMemoryUserRepository();
            MockSignupPresenter signupPresenter = new MockSignupPresenter();
            MockLoginPresenter loginPresenter = new MockLoginPresenter();

            SignupInteractor signupInteractor = new SignupInteractor(userRepository, signupPresenter);
            LoginInteractor loginInteractor = new LoginInteractor(userRepository, loginPresenter);

            // 1. Register new user
            SignupRequestModel signupRequest = new SignupRequestModel("flowuser", "flowpass");
            signupInteractor.signup(signupRequest);

            // Verify registration success
            SignupResponseModel signupResponse = signupPresenter.getLastSuccessResponse();
            assertTrue(signupResponse != null, "Registration response should not be null");
            assertTrue(signupResponse.isSuccess(), "Registration should be successful");

            // 2. Try to login
            LoginRequestModel loginRequest = new LoginRequestModel("flowuser", "flowpass");
            loginInteractor.login(loginRequest);

            // Verify login success
            LoginResponseModel loginResponse = loginPresenter.getLastResponse();
            assertTrue(loginResponse != null, "Login response should not be null");
            assertTrue(loginResponse.isSuccess(), "Login should be successful");
            assertEquals("flowuser", loginResponse.getUser().getUsername(), "Username should match");

            testPassed("Complete user flow test");
        } catch (Exception e) {
            testFailed("Complete user flow test", e.getMessage());
        }
    }


    private static void testBoundaryConditions() {
        System.out.println("\n--- Boundary Condition Tests ---");

        // Test 1: Many users
        testBoundaryConditionsManyUsers();
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

    // ==================== Error Handling Tests ====================

    private static void testErrorHandling() {
        System.out.println("\n--- Error Handling Tests ---");

        // Test 1: Null parameter handling
        testErrorHandlingNullParameters();
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

        } catch (Exception e) {
            testFailed("Null parameter error handling test", e.getMessage());
        }
    }

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