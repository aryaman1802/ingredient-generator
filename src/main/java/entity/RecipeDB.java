package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeDB {
    private String name;
    private String area; // cuisine/area
    private String category; // breakfast, etc. (when available)
    private Diet diet = Diet.NONE;
    private final List<String> ingredients = new ArrayList<>();
    private final List<String> instructions = new ArrayList<>();

    public RecipeDB() {}

    public RecipeDB(String name) { this.name = name; }

    public String getName() { return name; }
    public String getArea() { return area; }
    public String getCategory() { return category; }
    public Diet getDiet() { return diet; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getInstructions() { return instructions; }

    public void setName(String name) { this.name = name; }
    public void setArea(String area) { this.area = area; }
    public void setCategory(String category) { this.category = category; }
    public void setDiet(Diet diet) { this.diet = diet; }

    public void addIngredient(String line) {
        if (line != null && !line.isBlank()) ingredients.add(line.trim());
    }

    public void addInstruction(String step) {
        if (step != null && !step.isBlank()) instructions.add(step.trim());
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeDB)) return false;
        RecipeDB r = (RecipeDB) o;
        return Objects.equals(name, r.name);
    }

    @Override public int hashCode() { return Objects.hash(name); }
}
