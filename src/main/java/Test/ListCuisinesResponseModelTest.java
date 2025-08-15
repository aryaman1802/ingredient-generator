package Test;

import use_case.cuisines.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ListCuisinesResponseModel Tests")
class ListCuisinesResponseModelTest {

    @Test
    @DisplayName("test constructor and getter with normal list")
    void testConstructorAndGetterWithNormalList() {
        List<String> areas = Arrays.asList("Italian", "Chinese", "Mexican");
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        
        assertNotNull(model.getAreas());
        assertEquals(3, model.getAreas().size());
        assertEquals("Italian", model.getAreas().get(0));
        assertEquals("Chinese", model.getAreas().get(1));
        assertEquals("Mexican", model.getAreas().get(2));
    }

    @Test
    @DisplayName("test constructor and getter with empty list")
    void testConstructorAndGetterWithEmptyList() {
        List<String> areas = new ArrayList<>();
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        
        assertNotNull(model.getAreas());
        assertTrue(model.getAreas().isEmpty());
    }

    @Test
    @DisplayName("test constructor and getter with single element")
    void testConstructorAndGetterWithSingleElement() {
        List<String> areas = Collections.singletonList("Italian");
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        
        assertNotNull(model.getAreas());
        assertEquals(1, model.getAreas().size());
        assertEquals("Italian", model.getAreas().get(0));
    }

    @Test
    @DisplayName("test constructor and getter with null")
    void testConstructorAndGetterWithNull() {
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(null);
        
        assertNull(model.getAreas());
    }

    @Test
    @DisplayName("test list mutability")
    void testListMutability() {
        List<String> areas = new ArrayList<>(Arrays.asList("Italian", "Chinese"));
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        

        List<String> retrievedAreas = model.getAreas();

        retrievedAreas.add("Mexican");
        

        assertEquals(3, model.getAreas().size());
        assertTrue(model.getAreas().contains("Mexican"));
    }

    @Test
    @DisplayName("test original list modification")
    void testOriginalListModification() {
        List<String> areas = new ArrayList<>(Arrays.asList("Italian", "Chinese"));
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        

        areas.add("Mexican");
        areas.remove("Italian");
        

        assertEquals(2, model.getAreas().size());
        assertFalse(model.getAreas().contains("Italian"));
        assertTrue(model.getAreas().contains("Mexican"));
    }

    @Test
    @DisplayName("test list with duplicates")
    void testListWithDuplicates() {
        List<String> areas = Arrays.asList("Italian", "Chinese", "Italian", "Mexican", "Chinese");
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        
        assertNotNull(model.getAreas());
        assertEquals(5, model.getAreas().size());
        

        long italianCount = model.getAreas().stream()
            .filter(area -> "Italian".equals(area))
            .count();
        assertEquals(2, italianCount);
        
        long chineseCount = model.getAreas().stream()
            .filter(area -> "Chinese".equals(area))
            .count();
        assertEquals(2, chineseCount);
    }

    @Test
    @DisplayName("test list with null elements")
    void testListWithNullElements() {
        List<String> areas = Arrays.asList("Italian", null, "Chinese", null, "Mexican");
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        
        assertNotNull(model.getAreas());
        assertEquals(5, model.getAreas().size());
        

        assertNull(model.getAreas().get(1));
        assertNull(model.getAreas().get(3));
        

        assertEquals("Italian", model.getAreas().get(0));
        assertEquals("Chinese", model.getAreas().get(2));
        assertEquals("Mexican", model.getAreas().get(4));
    }

    @Test
    @DisplayName("test list with empty strings")
    void testListWithEmptyStrings() {
        List<String> areas = Arrays.asList("Italian", "", "Chinese", "  ", "Mexican");
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        
        assertNotNull(model.getAreas());
        assertEquals(5, model.getAreas().size());
        
        assertEquals("", model.getAreas().get(1));
        assertEquals("  ", model.getAreas().get(3));
    }

    @Test
    @DisplayName("test large list")
    void testLargeList() {
        List<String> areas = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            areas.add("Area" + i);
        }
        
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        
        assertNotNull(model.getAreas());
        assertEquals(1000, model.getAreas().size());
        assertEquals("Area0", model.getAreas().get(0));
        assertEquals("Area999", model.getAreas().get(999));
    }

    @Test
    @DisplayName("test different list implementations")
    void testDifferentListImplementations() {
        // ArrayList
        List<String> arrayList = new ArrayList<>(Arrays.asList("Italian", "Chinese"));
        ListCuisinesResponseModel model1 = new ListCuisinesResponseModel(arrayList);
        assertEquals(2, model1.getAreas().size());
        
        // LinkedList
        List<String> linkedList = new java.util.LinkedList<>(Arrays.asList("Italian", "Chinese"));
        ListCuisinesResponseModel model2 = new ListCuisinesResponseModel(linkedList);
        assertEquals(2, model2.getAreas().size());
        
        // Unmodifiable List
        List<String> unmodifiableList = Collections.unmodifiableList(
            Arrays.asList("Italian", "Chinese")
        );
        ListCuisinesResponseModel model3 = new ListCuisinesResponseModel(unmodifiableList);
        assertEquals(2, model3.getAreas().size());
    }

    @Test
    @DisplayName("test getter consistency")
    void testGetterConsistency() {
        List<String> areas = Arrays.asList("Italian", "Chinese", "Mexican");
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        

        List<String> firstCall = model.getAreas();
        List<String> secondCall = model.getAreas();
        List<String> thirdCall = model.getAreas();
        
        assertSame(firstCall, secondCall);
        assertSame(secondCall, thirdCall);
        assertEquals(firstCall, thirdCall);
    }

    @Test
    @DisplayName("test special character areas")
    void testSpecialCharacterAreas() {
        List<String> areas = Arrays.asList(
            "Côte d'Ivoire",
            "São Paulo",
            "日本料理",
            "한국 요리",
            "Русская кухня",
            "🍕 Italian",
            "Chinese (中国菜)"
        );
        
        ListCuisinesResponseModel model = new ListCuisinesResponseModel(areas);
        
        assertNotNull(model.getAreas());
        assertEquals(7, model.getAreas().size());
        assertEquals("Côte d'Ivoire", model.getAreas().get(0));
        assertEquals("日本料理", model.getAreas().get(2));
        assertEquals("🍕 Italian", model.getAreas().get(5));
    }
}