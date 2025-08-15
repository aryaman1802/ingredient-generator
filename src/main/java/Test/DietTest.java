package Test;

import entity.Diet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Diet Enum Tests")
class DietTest {

    @Test
    @DisplayName("test enum values")
    void testEnumValues() {
        Diet[] values = Diet.values();
        assertEquals(4, values.length);
        assertEquals(Diet.NONE, Diet.valueOf("NONE"));
        assertEquals(Diet.VEGETARIAN, Diet.valueOf("VEGETARIAN"));
        assertEquals(Diet.NON_VEGETARIAN, Diet.valueOf("NON_VEGETARIAN"));
        assertEquals(Diet.VEGAN, Diet.valueOf("VEGAN"));
    }

    @Test
    @DisplayName("test enumlabels")
    void testEnumLabels() {
        assertEquals("none", Diet.NONE.label);
        assertEquals("veg", Diet.VEGETARIAN.label);
        assertEquals("non-veg", Diet.NON_VEGETARIAN.label);
        assertEquals("vegan", Diet.VEGAN.label);
    }

    @ParameterizedTest
    @DisplayName("test fromLabel method - normal case")
    @CsvSource({
        "none, NONE",
        "NONE, NONE",
        "None, NONE",
        "veg, VEGETARIAN",
        "VEG, VEGETARIAN",
        "vegetarian, VEGETARIAN",
        "VEGETARIAN, VEGETARIAN",
        "non-veg, NON_VEGETARIAN",
        "NON-VEG, NON_VEGETARIAN",
        "non, NON_VEGETARIAN",
        "NON, NON_VEGETARIAN",
        "vegan, VEGAN",
        "VEGAN, VEGAN",
        "Vegan, VEGAN"
    })
    void testFromLabelNormalCases(String input, Diet expected) {
        assertEquals(expected, Diet.fromLabel(input));
    }

    @Test
    @DisplayName("test fromLabel method - null input")
    void testFromLabelNull() {
        assertEquals(Diet.NONE, Diet.fromLabel(null));
    }

    @ParameterizedTest
    @DisplayName("test fromLabel method - edge case")
    @ValueSource(strings = {
        "none123",
        "vegan_diet",
        "non-meat",
        "veggie",
        "vegetable"
    })
    void testFromLabelBoundaryCases(String input) {
        Diet result = Diet.fromLabel(input);
        
        if (input.startsWith("none")) {
            assertEquals(Diet.NONE, result);
        } else if (input.startsWith("vegan")) {
            assertEquals(Diet.VEGAN, result);
        } else if (input.startsWith("non")) {
            assertEquals(Diet.NON_VEGETARIAN, result);
        } else if (input.startsWith("veg")) {
            assertEquals(Diet.VEGETARIAN, result);
        }
    }

    @ParameterizedTest
    @DisplayName("test fromLabel method - invalid input")
    @ValueSource(strings = {
        "",
        " ",
        "invalid",
        "meat",
        "fish",
        "unknown",
        "123",
        "!@#$"
    })
    void testFromLabelInvalidInputs(String input) {
        assertEquals(Diet.NONE, Diet.fromLabel(input));
    }

    @Test
    @DisplayName("test fromLabel method - prefix only")
    void testFromLabelPrefixOnly() {
        assertEquals(Diet.VEGETARIAN, Diet.fromLabel("veg"));
        assertEquals(Diet.NON_VEGETARIAN, Diet.fromLabel("non"));
    }

    @Test
    @DisplayName("test fromLabel method - mixed case")
    void testFromLabelMixedCase() {
        assertEquals(Diet.VEGAN, Diet.fromLabel("VeGaN"));
        assertEquals(Diet.VEGETARIAN, Diet.fromLabel("VeGeTaRiAn"));
        assertEquals(Diet.NON_VEGETARIAN, Diet.fromLabel("NoN-VeG"));
        assertEquals(Diet.NONE, Diet.fromLabel("NoNe"));
    }

    @Test
    @DisplayName("test fromLabel method - space and special characters")
    void testFromLabelWithSpacesAndSpecialChars() {

        assertEquals(Diet.NONE, Diet.fromLabel("  vegan  "));
        assertEquals(Diet.NONE, Diet.fromLabel("  veg  "));
        assertEquals(Diet.NONE, Diet.fromLabel("  non  "));
        assertEquals(Diet.NONE, Diet.fromLabel("  none  "));
    }

    @Test
    @DisplayName("test enumordinal")
    void testEnumOrdinal() {
        assertEquals(0, Diet.NONE.ordinal());
        assertEquals(1, Diet.VEGETARIAN.ordinal());
        assertEquals(2, Diet.NON_VEGETARIAN.ordinal());
        assertEquals(3, Diet.VEGAN.ordinal());
    }

    @Test
    @DisplayName("test enum name method")
    void testEnumName() {
        assertEquals("NONE", Diet.NONE.name());
        assertEquals("VEGETARIAN", Diet.VEGETARIAN.name());
        assertEquals("NON_VEGETARIAN", Diet.NON_VEGETARIAN.name());
        assertEquals("VEGAN", Diet.VEGAN.name());
    }

    @Test
    @DisplayName("test enum toString method function")
    void testEnumToString() {
        assertEquals("NONE", Diet.NONE.toString());
        assertEquals("VEGETARIAN", Diet.VEGETARIAN.toString());
        assertEquals("NON_VEGETARIAN", Diet.NON_VEGETARIAN.toString());
        assertEquals("VEGAN", Diet.VEGAN.toString());
    }
}