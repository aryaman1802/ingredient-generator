package Test;


import java.io.*;
import java.nio.file.*;
import java.util.*;


public class MealDBSwingAppHistoryTest {
    
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    

    private static class MockRecipeGateway implements RecipeGateway {
        private List<Recipe> mockRecipes;
        private boolean shouldFail = false;
        private int searchCallCount = 0;
        private int randomCallCount = 0;
        
        public MockRecipeGateway() {
            this.mockRecipes = createMockRecipes();
        }
        
        @Override
        public List<Recipe> search(String query, String mealType, String cuisine, Diet diet) throws Exception {
            searchCallCount++;
            if (shouldFail) {
                throw new Exception("mock search fail");
            }
            

            saveSearchHistory(query, mealType, cuisine, diet);
            

            List<Recipe> filtered = new ArrayList<>();
            for (Recipe r : mockRecipes) {
                if (diet == Diet.NONE || r.diet == diet) {
                    filtered.add(r);
                }
            }
            return filtered.size() > 3 ? filtered.subList(0, 3) : filtered;
        }
        
        @Override
        public Recipe fetchRandom() throws Exception {
            randomCallCount++;
            if (shouldFail) {
                throw new Exception("mock get random recipe fail");
            }
            

            Recipe randomRecipe = mockRecipes.get(new Random().nextInt(mockRecipes.size()));
            saveRandomHistory(randomRecipe);
            return randomRecipe;
        }
        
        private List<Recipe> createMockRecipes() {
            List<Recipe> recipes = new ArrayList<>();

            Recipe vegan = new Recipe();
            vegan.name = "Vegan Salad";
            vegan.area = "Mediterranean";
            vegan.ingredients = Arrays.asList("Lettuce", "Tomato", "Cucumber", "Olive Oil");
            vegan.instructions = Arrays.asList("Chop vegetables", "Mix together", "Add dressing");
            vegan.diet = Diet.VEGAN;
            recipes.add(vegan);
            

            Recipe nonVeg = new Recipe();
            nonVeg.name = "Chicken Stir Fry";
            nonVeg.area = "Chinese";
            nonVeg.ingredients = Arrays.asList("Chicken", "Soy Sauce", "Vegetables", "Oil");
            nonVeg.instructions = Arrays.asList("Cut chicken", "Heat oil", "Stir fry ingredients");
            nonVeg.diet = Diet.NON_VEGETARIAN;
            recipes.add(nonVeg);
            

            Recipe vegetarian = new Recipe();
            vegetarian.name = "Cheese Pizza";
            vegetarian.area = "Italian";
            vegetarian.ingredients = Arrays.asList("Flour", "Cheese", "Tomato Sauce", "Yeast");
            vegetarian.instructions = Arrays.asList("Make dough", "Add toppings", "Bake in oven");
            vegetarian.diet = Diet.VEGETARIAN;
            recipes.add(vegetarian);
            
            return recipes;
        }
        

        private void saveSearchHistory(String query, String mealType, String cuisine, Diet diet) {
            try {
                Path historyPath = Paths.get("test_search_history.txt");
                String timestamp = new Date().toString();
                String entry = String.format("%s | Query: %s | Meal: %s | Cuisine: %s | Diet: %s%n",
                    timestamp, query, mealType, cuisine, diet.label);
                Files.write(historyPath, entry.getBytes(), 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveRandomHistory(Recipe recipe) {
            try {
                Path historyPath = Paths.get("test_random_history.txt");
                String timestamp = new Date().toString();
                String entry = String.format("%s | Random Recipe: %s | Area: %s%n",
                    timestamp, recipe.name, recipe.area);
                Files.write(historyPath, entry.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public void setShouldFail(boolean fail) {
            this.shouldFail = fail;
        }
        
        public int getSearchCallCount() {
            return searchCallCount;
        }
        
        public int getRandomCallCount() {
            return randomCallCount;
        }
    }
    

    static class Recipe {
        String name;
        String area;
        List<String> ingredients;
        List<String> instructions;
        Diet diet;
    }
    

    enum Diet {
        NONE("none"), VEGETARIAN("veg"), NON_VEGETARIAN("non-veg"), VEGAN("vegan");
        public final String label;
        Diet(String l) { this.label = l; }
    }
    

    interface RecipeGateway {
        List<Recipe> search(String query, String mealType, String cuisine, Diet diet) throws Exception;
        Recipe fetchRandom() throws Exception;
    }
    
    public static void main(String[] args) {
        System.out.println("========== MealDBSwingApp 历史功能测试 ==========\n");
        

        cleanupTestFiles();
        

        testSaveSearchHistory();
        testSaveRandomRecipeHistory();
        testLoadSearchHistory();
        testLoadRandomHistory();
        testHistoryFileCreation();
        testHistoryWithMultipleEntries();
        testHistoryWithDifferentDiets();
        testHistoryPersistence();
        testHistoryWithErrors();
        testConcurrentHistoryAccess();
        

        printTestSummary();


        cleanupTestFiles();
    }
    

    private static void testSaveSearchHistory() {
        String testName = "test search history save";
        totalTests++;
        
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            

            gateway.search("chicken", "Dinner", "Chinese", Diet.NON_VEGETARIAN);
            

            Path historyPath = Paths.get("test_search_history.txt");
            if (Files.exists(historyPath)) {
                String content = new String(Files.readAllBytes(historyPath));
                if (content.contains("chicken") && content.contains("Dinner") && 
                    content.contains("Chinese") && content.contains("non-veg")) {
                    passedTests++;
                    System.out.println("✓ " + testName + " - 通过");
                } else {
                    failedTests++;
                    System.out.println("✗ " + testName + " - 失败: 历史内容不完整");
                }
            } else {
                failedTests++;
                System.out.println("✗ " + testName + " - 失败: 历史文件未创建");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("✗ " + testName + " - 失败: " + e.getMessage());
        }
    }

    private static void testSaveRandomRecipeHistory() {
        String testName = "test random recipe histoy";
        totalTests++;
        
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            

            Recipe recipe = gateway.fetchRandom();
            

            Path historyPath = Paths.get("test_random_history.txt");
            if (Files.exists(historyPath)) {
                String content = new String(Files.readAllBytes(historyPath));
                if (content.contains("Random Recipe") && content.contains(recipe.name)) {
                    passedTests++;
                    System.out.println("✓ " + testName + " - pass");
                } else {
                    failedTests++;
                    System.out.println("✗ " + testName + " - fail: history incorrect");
                }
            } else {
                failedTests++;
                System.out.println("✗ " + testName + " - fail: history create fail");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("✗ " + testName + " - fail: " + e.getMessage());
        }
    }
    

    private static void testLoadSearchHistory() {
        String testName = "test loading history";
        totalTests++;
        
        try {

            MockRecipeGateway gateway = new MockRecipeGateway();
            gateway.search("pasta", "Lunch", "Italian", Diet.VEGETARIAN);
            gateway.search("rice", "Dinner", "Asian", Diet.VEGAN);
            

            Path historyPath = Paths.get("test_search_history.txt");
            List<String> lines = Files.readAllLines(historyPath);
            
            if (lines.size() >= 2) {
                boolean hasPasta = lines.stream().anyMatch(line -> line.contains("pasta"));
                boolean hasRice = lines.stream().anyMatch(line -> line.contains("rice"));
                
                if (hasPasta && hasRice) {
                    passedTests++;
                    System.out.println("✓ " + testName + " - pass");
                } else {
                    failedTests++;
                    System.out.println("✗ " + testName + " - fail: history not complete");
                }
            } else {
                failedTests++;
                System.out.println("✗ " + testName + " - test: not enough history");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("✗ " + testName + " - fail: " + e.getMessage());
        }
    }
    

    private static void testLoadRandomHistory() {
        String testName = "test adding random history";
        totalTests++;
        
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();

            for (int i = 0; i < 3; i++) {
                gateway.fetchRandom();
            }
            

            Path historyPath = Paths.get("test_random_history.txt");
            List<String> lines = Files.readAllLines(historyPath);
            
            if (lines.size() >= 3) {
                boolean allHaveTimestamp = lines.stream()
                    .allMatch(line -> line.matches(".*\\d{4}.*Random Recipe.*"));
                
                if (allHaveTimestamp) {
                    passedTests++;
                    System.out.println("✓ " + testName + " - pass");
                } else {
                    failedTests++;
                    System.out.println("✗ " + testName + " - fail: history format not correct");
                }
            } else {
                failedTests++;
                System.out.println("✗ " + testName + " - fail: random history not enough");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("✗ " + testName + " - fail: " + e.getMessage());
        }
    }
    

    private static void testHistoryFileCreation() {
        String testName = "test history file creation";
        totalTests++;
        
        try {

            Files.deleteIfExists(Paths.get("test_search_history.txt"));
            Files.deleteIfExists(Paths.get("test_random_history.txt"));
            
            MockRecipeGateway gateway = new MockRecipeGateway();
            

            gateway.search("test", "Breakfast", "Any", Diet.NONE);
            gateway.fetchRandom();
            
            boolean searchFileExists = Files.exists(Paths.get("test_search_history.txt"));
            boolean randomFileExists = Files.exists(Paths.get("test_random_history.txt"));
            
            if (searchFileExists && randomFileExists) {
                passedTests++;
                System.out.println("✓ " + testName + " - pass");
            } else {
                failedTests++;
                System.out.println("✗ " + testName + " - fail: file not automatically created");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("✗ " + testName + " - fail: " + e.getMessage());
        }
    }
    

    private static void testHistoryWithMultipleEntries() {
        String testName = "test multiple history";
        totalTests++;
        
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            

            for (int i = 0; i < 10; i++) {
                gateway.search("ingredient" + i, "Lunch", "Any", Diet.NONE);
            }
            
            Path historyPath = Paths.get("test_search_history.txt");
            List<String> lines = Files.readAllLines(historyPath);
            
            if (lines.size() >= 10) {

                boolean allPresent = true;
                for (int i = 0; i < 10; i++) {
                    String expectedIngredient = "ingredient" + i;
                    boolean found = lines.stream()
                        .anyMatch(line -> line.contains(expectedIngredient));
                    if (!found) {
                        allPresent = false;
                        break;
                    }
                }
                
                if (allPresent) {
                    passedTests++;
                    System.out.println("✓ " + testName + " - pass");
                } else {
                    failedTests++;
                    System.out.println("✗ " + testName + " - fail: some history lost");
                }
            } else {
                failedTests++;
                System.out.println("✗ " + testName + " - fail: not correct number of history");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("✗ " + testName + " - fail: " + e.getMessage());
        }
    }
    

    private static void testHistoryWithDifferentDiets() {
        String testName = "test history with different diets";
        totalTests++;
        
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            

            gateway.search("vegan food", "Breakfast", "Any", Diet.VEGAN);
            gateway.search("vegetarian food", "Lunch", "Any", Diet.VEGETARIAN);
            gateway.search("meat food", "Dinner", "Any", Diet.NON_VEGETARIAN);
            gateway.search("any food", "Breakfast", "Any", Diet.NONE);
            
            Path historyPath = Paths.get("test_search_history.txt");
            String content = new String(Files.readAllBytes(historyPath));
            
            boolean hasVegan = content.contains("vegan");
            boolean hasVegetarian = content.contains("veg") && !content.contains("non-veg");
            boolean hasNonVeg = content.contains("non-veg");
            boolean hasNone = content.contains("none");
            
            if (hasVegan && hasVegetarian && hasNonVeg && hasNone) {
                passedTests++;
                System.out.println("✓ " + testName + " - pass");
            } else {
                failedTests++;
                System.out.println("✗ " + testName + " - fail: not complete history");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("✗ " + testName + " - fail: " + e.getMessage());
        }
    }
    

    private static void testHistoryPersistence() {
        String testName = "history persistence";
        totalTests++;
        
        try {

            MockRecipeGateway gateway1 = new MockRecipeGateway();
            gateway1.search("persistent test", "Dinner", "Any", Diet.NONE);
            

            MockRecipeGateway gateway2 = new MockRecipeGateway();
            gateway2.search("second test", "Lunch", "Any", Diet.NONE);
            

            Path historyPath = Paths.get("test_search_history.txt");
            String content = new String(Files.readAllBytes(historyPath));
            
            if (content.contains("persistent test") && content.contains("second test")) {
                passedTests++;
                System.out.println("✓ " + testName + " - pass");
            } else {
                failedTests++;
                System.out.println("✗ " + testName + " - fail: history not persistent");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("✗ " + testName + " - fail: " + e.getMessage());
        }
    }
    

    private static void testHistoryWithErrors() {
        String testName = "test history with errors";
        totalTests++;
        
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            

            gateway.search("normal", "Lunch", "Any", Diet.NONE);
            

            gateway.setShouldFail(true);
            

            try {
                gateway.search("fail", "Dinner", "Any", Diet.NONE);
            } catch (Exception e) {

            }
            

            gateway.setShouldFail(false);
            gateway.search("after error", "Breakfast", "Any", Diet.NONE);
            

            Path historyPath = Paths.get("test_search_history.txt");
            String content = new String(Files.readAllBytes(historyPath));
            

            if (content.contains("normal") && content.contains("after error") && 
                !content.contains("fail")) {
                passedTests++;
                System.out.println("✓ " + testName + " - pass");
            } else {
                failedTests++;
                System.out.println("✗ " + testName + " - fail: error handling not correct");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("✗ " + testName + " - fail: " + e.getMessage());
        }
    }
    

    private static void testConcurrentHistoryAccess() {
        String testName = "test current history access";
        totalTests++;
        
        try {
            MockRecipeGateway gateway = new MockRecipeGateway();
            List<Thread> threads = new ArrayList<>();
            

            for (int i = 0; i < 5; i++) {
                final int threadId = i;
                Thread t = new Thread(() -> {
                    try {
                        gateway.search("thread" + threadId, "Lunch", "Any", Diet.NONE);
                        gateway.fetchRandom();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                threads.add(t);
                t.start();
            }
            

            for (Thread t : threads) {
                t.join();
            }
            

            Path searchPath = Paths.get("test_search_history.txt");
            Path randomPath = Paths.get("test_random_history.txt");
            
            List<String> searchLines = Files.readAllLines(searchPath);
            List<String> randomLines = Files.readAllLines(randomPath);
            

            if (searchLines.size() >= 5 && randomLines.size() >= 5) {
                passedTests++;
                System.out.println("✓ " + testName + " - pass");
            } else {
                failedTests++;
                System.out.println("✗ " + testName + " - fail: history not complete");
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("✗ " + testName + " - fail: " + e.getMessage());
        }
    }
    

    private static void cleanupTestFiles() {
        try {
            Files.deleteIfExists(Paths.get("test_search_history.txt"));
            Files.deleteIfExists(Paths.get("test_random_history.txt"));
        } catch (IOException e) {
            System.err.println("clear test history fail: " + e.getMessage());
        }
    }

    private static void printTestSummary() {
        System.out.println("\n");
        System.out.println("total test: " + totalTests);
        System.out.println("pass: " + passedTests);
        System.out.println("fail: " + failedTests);
        
        double passRate = (double) passedTests / totalTests * 100;
        System.out.printf("passed percentage: %.2f%%\n", passRate);
        
        if (failedTests == 0) {
            System.out.println("\n✓ all test passed！");
        } else {
            System.out.println("\n✗ have " + failedTests + " number of failed tests");
        }
    }
}