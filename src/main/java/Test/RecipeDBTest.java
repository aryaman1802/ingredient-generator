package Test;

import entity.RecipeDB;
import entity.Diet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RecipeDB Entity Tests")
class RecipeDBTest {

    private RecipeDB recipe;

    @BeforeEach
    void setUp() {
        recipe = new RecipeDB();
    }

    @Test
    @DisplayName("test constructor")
    void testDefaultConstructor() {
        assertNull(recipe.getName());
        assertNull(recipe.getArea());
        assertNull(recipe.getCategory());
        assertEquals(Diet.NONE, recipe.getDiet());
        assertNotNull(recipe.getIngredients());
        assertTrue(recipe.getIngredients().isEmpty());
        assertNotNull(recipe.getInstructions());
        assertTrue(recipe.getInstructions().isEmpty());
    }

    @Test
    @DisplayName("test constructor with name")
    void testConstructorWithName() {
        RecipeDB namedRecipe = new RecipeDB("Pasta Carbonara");
        assertEquals("Pasta Carbonara", namedRecipe.getName());
        assertNull(namedRecipe.getArea());
        assertNull(namedRecipe.getCategory());
        assertEquals(Diet.NONE, namedRecipe.getDiet());
        assertNotNull(namedRecipe.getIngredients());
        assertTrue(namedRecipe.getIngredients().isEmpty());
        assertNotNull(namedRecipe.getInstructions());
        assertTrue(namedRecipe.getInstructions().isEmpty());
    }

    @Test
    @DisplayName("test name getter and setter")
    void testNameGetterSetter() {
        assertNull(recipe.getName());
        
        recipe.setName("Chicken Curry");
        assertEquals("Chicken Curry", recipe.getName());
        
        recipe.setName(null);
        assertNull(recipe.getName());
        
        recipe.setName("");
        assertEquals("", recipe.getName());
    }

    @Test
    @DisplayName("test area getter and setter")
    void testAreaGetterSetter() {
        assertNull(recipe.getArea());
        
        recipe.setArea("Italian");
        assertEquals("Italian", recipe.getArea());
        
        recipe.setArea("Chinese");
        assertEquals("Chinese", recipe.getArea());
        
        recipe.setArea(null);
        assertNull(recipe.getArea());
    }

    @Test
    @DisplayName("test category getter and setter")
    void testCategoryGetterSetter() {
        assertNull(recipe.getCategory());
        
        recipe.setCategory("Breakfast");
        assertEquals("Breakfast", recipe.getCategory());
        
        recipe.setCategory("Dinner");
        assertEquals("Dinner", recipe.getCategory());
        
        recipe.setCategory(null);
        assertNull(recipe.getCategory());
    }

    @Test
    @DisplayName("test diet getter and setter")
    void testDietGetterSetter() {
        assertEquals(Diet.NONE, recipe.getDiet());
        
        recipe.setDiet(Diet.VEGAN);
        assertEquals(Diet.VEGAN, recipe.getDiet());
        
        recipe.setDiet(Diet.VEGETARIAN);
        assertEquals(Diet.VEGETARIAN, recipe.getDiet());
        
        recipe.setDiet(Diet.NON_VEGETARIAN);
        assertEquals(Diet.NON_VEGETARIAN, recipe.getDiet());
        
        recipe.setDiet(null);
        assertNull(recipe.getDiet());
    }

    @Test
    @DisplayName("test adding ordinary ingredient")
    void testAddIngredient() {
        assertTrue(recipe.getIngredients().isEmpty());
        
        recipe.addIngredient("2 cups flour");
        assertEquals(1, recipe.getIngredients().size());
        assertEquals("2 cups flour", recipe.getIngredients().get(0));
        
        recipe.addIngredient("1 tsp salt");
        assertEquals(2, recipe.getIngredients().size());
        assertEquals("1 tsp salt", recipe.getIngredients().get(1));
        
        recipe.addIngredient("3 eggs");
        assertEquals(3, recipe.getIngredients().size());
        assertEquals("3 eggs", recipe.getIngredients().get(2));
    }

    @Test
    @DisplayName("test ingredient with space")
    void testAddIngredientWithSpaces() {
        recipe.addIngredient("  2 cups flour  ");
        assertEquals(1, recipe.getIngredients().size());
        assertEquals("2 cups flour", recipe.getIngredients().get(0));
        
        recipe.addIngredient("\t1 tsp salt\t");
        assertEquals(2, recipe.getIngredients().size());
        assertEquals("1 tsp salt", recipe.getIngredients().get(1));
        
        recipe.addIngredient("  \n  3 eggs  \n  ");
        assertEquals(3, recipe.getIngredients().size());
        assertEquals("3 eggs", recipe.getIngredients().get(2));
    }

    @ParameterizedTest
    @DisplayName("test null or empty ingredient")
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n", "   \t\n   "})
    void testAddInvalidIngredient(String ingredient) {
        recipe.addIngredient(ingredient);
        assertTrue(recipe.getIngredients().isEmpty());
        
        recipe.addIngredient("valid ingredient");
        recipe.addIngredient(ingredient);
        assertEquals(1, recipe.getIngredients().size());
    }

    @Test
    @DisplayName("test adding regular procedure")
    void testAddInstruction() {
        assertTrue(recipe.getInstructions().isEmpty());
        
        recipe.addInstruction("Preheat oven to 350°F");
        assertEquals(1, recipe.getInstructions().size());
        assertEquals("Preheat oven to 350°F", recipe.getInstructions().get(0));
        
        recipe.addInstruction("Mix dry ingredients");
        assertEquals(2, recipe.getInstructions().size());
        assertEquals("Mix dry ingredients", recipe.getInstructions().get(1));
        
        recipe.addInstruction("Bake for 30 minutes");
        assertEquals(3, recipe.getInstructions().size());
        assertEquals("Bake for 30 minutes", recipe.getInstructions().get(2));
    }

    @Test
    @DisplayName("test procedure with space")
    void testAddInstructionWithSpaces() {
        recipe.addInstruction("  Preheat oven  ");
        assertEquals(1, recipe.getInstructions().size());
        assertEquals("Preheat oven", recipe.getInstructions().get(0));
        
        recipe.addInstruction("\tMix ingredients\t");
        assertEquals(2, recipe.getInstructions().size());
        assertEquals("Mix ingredients", recipe.getInstructions().get(1));
    }

    @ParameterizedTest
    @DisplayName("test adding null procedure")
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n", "   \t\n   "})
    void testAddInvalidInstruction(String instruction) {
        recipe.addInstruction(instruction);
        assertTrue(recipe.getInstructions().isEmpty());
        
        recipe.addInstruction("valid instruction");
        recipe.addInstruction(instruction);
        assertEquals(1, recipe.getInstructions().size());
    }

    @Test
    @DisplayName("test equals method - same object")
    void testEqualsSameObject() {
        recipe.setName("Test Recipe");
        assertTrue(recipe.equals(recipe));
    }

    @Test
    @DisplayName("test equals method - same name")
    void testEqualsSameName() {
        recipe.setName("Pasta");
        RecipeDB other = new RecipeDB("Pasta");
        
        assertTrue(recipe.equals(other));
        assertTrue(other.equals(recipe));
    }

    @Test
    @DisplayName("test equals method - different name")
    void testEqualsDifferentName() {
        recipe.setName("Pasta");
        RecipeDB other = new RecipeDB("Pizza");
        
        assertFalse(recipe.equals(other));
        assertFalse(other.equals(recipe));
    }

    @Test
    @DisplayName("test equals method - null name")
    void testEqualsNullName() {
        RecipeDB other = new RecipeDB();
        
        assertTrue(recipe.equals(other));
        assertTrue(other.equals(recipe));
        
        recipe.setName("Something");
        assertFalse(recipe.equals(other));
        assertFalse(other.equals(recipe));
    }

    @Test
    @DisplayName("test equals method- null object")
    void testEqualsNull() {
        assertFalse(recipe.equals(null));
    }

    @Test
    @DisplayName("test equals method - different type")
    void testEqualsDifferentType() {
        assertFalse(recipe.equals("Not a RecipeDB"));
        assertFalse(recipe.equals(42));
        assertFalse(recipe.equals(new Object()));
    }

    @Test
    @DisplayName("test equals method ignores other properties")
    void testEqualsIgnoresOtherProperties() {
        recipe.setName("Same Recipe");
        recipe.setArea("Italian");
        recipe.setCategory("Dinner");
        recipe.setDiet(Diet.VEGAN);
        recipe.addIngredient("Tomato");
        recipe.addInstruction("Cook");
        
        RecipeDB other = new RecipeDB("Same Recipe");
        other.setArea("Chinese");
        other.setCategory("Lunch");
        other.setDiet(Diet.NON_VEGETARIAN);
        other.addIngredient("Chicken");
        other.addInstruction("Fry");
        
        assertTrue(recipe.equals(other));
    }

    @Test
    @DisplayName("test hashCode method - same name")
    void testHashCodeSameName() {
        recipe.setName("Test Recipe");
        RecipeDB other = new RecipeDB("Test Recipe");
        
        assertEquals(recipe.hashCode(), other.hashCode());
    }

    @Test
    @DisplayName("test hashCode method - null name")
    void testHashCodeNullName() {
        RecipeDB other = new RecipeDB();
        assertEquals(recipe.hashCode(), other.hashCode());
    }

    @Test
    @DisplayName("test hashCode method - different name hashCode")
    void testHashCodeDifferentName() {
        recipe.setName("Recipe1");
        RecipeDB other = new RecipeDB("Recipe2");
        int hash1 = recipe.hashCode();
        int hash2 = recipe.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("test hashCode ignore other properties")
    void testHashCodeIgnoresOtherProperties() {
        recipe.setName("Recipe");
        recipe.setArea("Italian");
        recipe.setDiet(Diet.VEGAN);
        int hash1 = recipe.hashCode();
        
        recipe.setArea("Chinese");
        recipe.setDiet(Diet.NON_VEGETARIAN);
        recipe.addIngredient("New ingredient");
        int hash2 = recipe.hashCode();
        
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("test ingredients list mutability")
    void testIngredientsListMutability() {
        List<String> ingredients = recipe.getIngredients();
        ingredients.add("Direct add");
        
        assertEquals(1, recipe.getIngredients().size());
        assertEquals("Direct add", recipe.getIngredients().get(0));
    }

    @Test
    @DisplayName("test instructions list mutability")
    void testInstructionsListMutability() {
        List<String> instructions = recipe.getInstructions();
        instructions.add("Direct instruction");
        
        assertEquals(1, recipe.getInstructions().size());
        assertEquals("Direct instruction", recipe.getInstructions().get(0));
    }

    @Test
    @DisplayName("test full Recipe object creation")
    void testCompleteRecipeCreation() {
        RecipeDB fullRecipe = new RecipeDB("Spaghetti Bolognese");
        fullRecipe.setArea("Italian");
        fullRecipe.setCategory("Main Course");
        fullRecipe.setDiet(Diet.NON_VEGETARIAN);
        
        fullRecipe.addIngredient("500g spaghetti");
        fullRecipe.addIngredient("400g ground beef");
        fullRecipe.addIngredient("1 can tomatoes");
        fullRecipe.addIngredient("1 onion");
        fullRecipe.addIngredient("2 cloves garlic");
        
        fullRecipe.addInstruction("Boil water for pasta");
        fullRecipe.addInstruction("Brown the ground beef");
        fullRecipe.addInstruction("Add tomatoes and simmer");
        fullRecipe.addInstruction("Cook pasta according to package");
        fullRecipe.addInstruction("Combine and serve");
        
        assertEquals("Spaghetti Bolognese", fullRecipe.getName());
        assertEquals("Italian", fullRecipe.getArea());
        assertEquals("Main Course", fullRecipe.getCategory());
        assertEquals(Diet.NON_VEGETARIAN, fullRecipe.getDiet());
        assertEquals(5, fullRecipe.getIngredients().size());
        assertEquals(5, fullRecipe.getInstructions().size());
    }

    @Test
    @DisplayName("test large ingredients and instructions")
    void testLargeNumberOfIngredientsAndInstructions() {
        for (int i = 1; i <= 100; i++) {
            recipe.addIngredient("Ingredient " + i);
            recipe.addInstruction("Step " + i);
        }
        
        assertEquals(100, recipe.getIngredients().size());
        assertEquals(100, recipe.getInstructions().size());
        assertEquals("Ingredient 1", recipe.getIngredients().get(0));
        assertEquals("Ingredient 100", recipe.getIngredients().get(99));
        assertEquals("Step 1", recipe.getInstructions().get(0));
        assertEquals("Step 100", recipe.getInstructions().get(99));
    }
}