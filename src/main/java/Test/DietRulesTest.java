package Test;

import entity.Diet;
import entity.DietRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DietRules Tests")
class DietRulesTest {

    @Test
    @DisplayName("test private constructor")
    void testPrivateConstructor() throws Exception {
        Constructor<DietRules> constructor = DietRules.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }

    @Test
    @DisplayName("test empty ingredients list")
    void testEmptyIngredientsList() {
        Diet result = DietRules.inferDiet(Collections.emptyList());
        assertEquals(Diet.VEGAN, result);
    }

    @Test
    @DisplayName("test list with null elements")
    void testListWithNullElements() {
        List<String> ingredients = Arrays.asList("tomato", null, "potato");
        Diet result = DietRules.inferDiet(ingredients);
        assertEquals(Diet.VEGAN, result);
    }

    @ParameterizedTest
    @DisplayName("test non vege ingredient")
    @MethodSource("provideNonVegetarianIngredients")
    void testNonVegetarianIngredients(List<String> ingredients, String testCase) {
        Diet result = DietRules.inferDiet(ingredients);
        assertEquals(Diet.NON_VEGETARIAN, result, "Failed for: " + testCase);
    }

    static Stream<Arguments> provideNonVegetarianIngredients() {
        return Stream.of(

            Arguments.of(List.of("chicken breast"), "chicken"),
            Arguments.of(List.of("beef steak"), "beef"),
            Arguments.of(List.of("pork chops"), "pork"),
            Arguments.of(List.of("lamb shank"), "lamb"),
            Arguments.of(List.of("mutton curry"), "mutton"),
            Arguments.of(List.of("veal cutlet"), "veal"),
            Arguments.of(List.of("turkey breast"), "turkey"),
            Arguments.of(List.of("duck confit"), "duck"),
            

            Arguments.of(List.of("bacon strips"), "bacon"),
            Arguments.of(List.of("ham sandwich"), "ham"),
            Arguments.of(List.of("prosciutto"), "prosciutto"),
            Arguments.of(List.of("chorizo sausage"), "chorizo"),
            Arguments.of(List.of("pork sausage"), "sausage"),
            Arguments.of(List.of("meat pie"), "meat"),
            Arguments.of(List.of("ribeye steak"), "steak"),
            Arguments.of(List.of("ground beef mince"), "mince"),
            

            Arguments.of(List.of("anchovy paste"), "anchovy"),
            Arguments.of(List.of("fish fillet"), "fish"),
            Arguments.of(List.of("salmon sushi"), "salmon"),
            Arguments.of(List.of("tuna salad"), "tuna"),
            Arguments.of(List.of("cod fish"), "cod"),
            Arguments.of(List.of("haddock"), "haddock"),
            Arguments.of(List.of("sardine can"), "sardine"),
            Arguments.of(List.of("prawn cocktail"), "prawn"),
            Arguments.of(List.of("shrimp scampi"), "shrimp"),
            Arguments.of(List.of("crab cakes"), "crab"),
            Arguments.of(List.of("lobster tail"), "lobster"),
            Arguments.of(List.of("clam chowder"), "clam"),
            Arguments.of(List.of("mussel soup"), "mussel"),
            Arguments.of(List.of("oyster sauce"), "oyster"),
            Arguments.of(List.of("octopus salad"), "octopus"),
            Arguments.of(List.of("squid rings"), "squid"),
            

            Arguments.of(List.of("tomato", "chicken", "rice"), "mixed with chicken"),
            Arguments.of(List.of("potato", "beef", "onion"), "mixed with beef"),
            Arguments.of(List.of("lettuce", "tuna", "mayo"), "mixed with tuna")
        );
    }

    @ParameterizedTest
    @DisplayName("test vegetarian intgredients")
    @MethodSource("provideVegetarianIngredients")
    void testVegetarianIngredients(List<String> ingredients, String testCase) {
        Diet result = DietRules.inferDiet(ingredients);
        assertEquals(Diet.VEGETARIAN, result, "Failed for: " + testCase);
    }

    static Stream<Arguments> provideVegetarianIngredients() {
        return Stream.of(

            Arguments.of(List.of("egg"), "single egg"),
            Arguments.of(List.of("eggs"), "plural eggs"),
            Arguments.of(List.of("scrambled eggs"), "scrambled eggs"),
            

            Arguments.of(List.of("milk"), "milk"),
            Arguments.of(List.of("butter"), "butter"),
            Arguments.of(List.of("ghee"), "ghee"),
            Arguments.of(List.of("cheese"), "cheese"),
            Arguments.of(List.of("yoghurt"), "yoghurt"),
            Arguments.of(List.of("yogurt"), "yogurt"),
            Arguments.of(List.of("cream"), "cream"),
            

            Arguments.of(List.of("honey"), "honey"),
            Arguments.of(List.of("gelatin"), "gelatin"),
            Arguments.of(List.of("gelatine"), "gelatine"),
            

            Arguments.of(List.of("tomato", "egg", "bread"), "mixed with egg"),
            Arguments.of(List.of("pasta", "cheese", "basil"), "mixed with cheese"),
            Arguments.of(List.of("rice", "milk", "sugar"), "mixed with milk"),
            Arguments.of(List.of("flour", "butter", "sugar"), "mixed with butter")
        );
    }

    @ParameterizedTest
    @DisplayName("test vegan ingredients")
    @MethodSource("provideVeganIngredients")
    void testVeganIngredients(List<String> ingredients, String testCase) {
        Diet result = DietRules.inferDiet(ingredients);
        assertEquals(Diet.VEGAN, result, "Failed for: " + testCase);
    }

    static Stream<Arguments> provideVeganIngredients() {
        return Stream.of(
            Arguments.of(List.of("tomato"), "single vegetable"),
            Arguments.of(List.of("potato", "carrot", "onion"), "multiple vegetables"),
            Arguments.of(List.of("rice", "beans", "corn"), "grains and legumes"),
            Arguments.of(List.of("apple", "banana", "orange"), "fruits"),
            Arguments.of(List.of("tofu", "soy sauce", "tempeh"), "soy products"),
            Arguments.of(List.of("almond flour", "cashew nuts"), "plant-based alternatives"),
            Arguments.of(List.of("olive oil", "vinegar", "salt"), "condiments"),
            Arguments.of(List.of("spinach", "kale", "lettuce"), "leafy greens")
        );
    }

    @Test
    @DisplayName("test case insensitive")
    void testCaseInsensitive() {
        assertEquals(Diet.NON_VEGETARIAN, DietRules.inferDiet(List.of("CHICKEN")));
        assertEquals(Diet.NON_VEGETARIAN, DietRules.inferDiet(List.of("Chicken")));
        assertEquals(Diet.NON_VEGETARIAN, DietRules.inferDiet(List.of("ChIcKeN")));
        
        assertEquals(Diet.VEGETARIAN, DietRules.inferDiet(List.of("EGGS")));
        assertEquals(Diet.VEGETARIAN, DietRules.inferDiet(List.of("Eggs")));
        assertEquals(Diet.VEGETARIAN, DietRules.inferDiet(List.of("EgGs")));
    }

    @Test
    @DisplayName("test partial matching")
    void testPartialMatching() {

        assertEquals(Diet.NON_VEGETARIAN, DietRules.inferDiet(List.of("grilled chicken breast")));
        assertEquals(Diet.NON_VEGETARIAN, DietRules.inferDiet(List.of("beef-style stew")));
        assertEquals(Diet.VEGETARIAN, DietRules.inferDiet(List.of("egg-white omelette")));
        assertEquals(Diet.VEGETARIAN, DietRules.inferDiet(List.of("low-fat milk")));
    }

    @Test
    @DisplayName("test priority order")
    void testPriorityOrder() {

        assertEquals(Diet.NON_VEGETARIAN, 
            DietRules.inferDiet(List.of("chicken", "egg", "tomato")));

        assertEquals(Diet.NON_VEGETARIAN, 
            DietRules.inferDiet(List.of("beef", "milk", "potato")));
        

        assertEquals(Diet.VEGETARIAN, 
            DietRules.inferDiet(List.of("egg", "tomato", "rice")));
    }

    @Test
    @DisplayName("test special character and numbers")
    void testSpecialCharactersAndNumbers() {

        assertEquals(Diet.VEGAN, DietRules.inferDiet(List.of("tomato123", "potato#", "rice@")));
        assertEquals(Diet.NON_VEGETARIAN, DietRules.inferDiet(List.of("chicken123")));
        assertEquals(Diet.VEGETARIAN, DietRules.inferDiet(List.of("egg#special")));
    }

    @Test
    @DisplayName("test long ingredients list")
    void testLongIngredientsList() {

        List<String> veganList = Collections.nCopies(100, "tomato");
        assertEquals(Diet.VEGAN, DietRules.inferDiet(veganList));
        

        List<String> mixedList = new java.util.ArrayList<>(Collections.nCopies(99, "tomato"));
        mixedList.add("chicken");
        assertEquals(Diet.NON_VEGETARIAN, DietRules.inferDiet(mixedList));
    }

    @Test
    @DisplayName("test edge case, ingredient name in another ingredient")
    void testFalsePositives() {

        assertEquals(Diet.VEGAN, DietRules.inferDiet(List.of("hamster food")));
        

        assertEquals(Diet.VEGAN, DietRules.inferDiet(List.of("eggplant")));
        

        assertEquals(Diet.NON_VEGETARIAN, DietRules.inferDiet(List.of("hamburger"))); // 包含"ham"
    }

    @Test
    @DisplayName("test pipe separator")
    void testPipeSeparator() {

        List<String> ingredients = List.of("tomato | potato", "carrot | onion");
        Diet result = DietRules.inferDiet(ingredients);
        assertEquals(Diet.VEGAN, result);
        

        ingredients = List.of("chicken | rice");
        result = DietRules.inferDiet(ingredients);
        assertEquals(Diet.NON_VEGETARIAN, result);
    }

    @Test
    @DisplayName("test all non vegetarian key words")
    void testAllNonVegKeywords() {
        String[] nonVegKeywords = {
            "chicken","beef","pork","lamb","mutton","veal","turkey","duck",
            "bacon","ham","prosciutto","chorizo","sausage","meat","steak",
            "mince","anchovy","fish","salmon","tuna","cod","haddock","sardine",
            "prawn","shrimp","crab","lobster","clam","mussel","oyster","octopus","squid"
        };
        
        for (String keyword : nonVegKeywords) {
            Diet result = DietRules.inferDiet(List.of(keyword));
            assertEquals(Diet.NON_VEGETARIAN, result, 
                "Failed to identify '" + keyword + "' as non-vegetarian");
        }
    }

    @Test
    @DisplayName("test all vegetarian key words")
    void testAllVegetarianKeywords() {
        String[] vegetarianKeywords = {
            "egg","eggs","milk","butter","ghee","cheese","yoghurt","yogurt",
            "cream","honey","gelatin","gelatine"
        };
        
        for (String keyword : vegetarianKeywords) {
            Diet result = DietRules.inferDiet(List.of(keyword));
            assertEquals(Diet.VEGETARIAN, result, 
                "Failed to identify '" + keyword + "' as vegetarian");
        }
    }
}