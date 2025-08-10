package entity;

public enum Diet {
    NONE("none"),
    VEGETARIAN("veg"),
    NON_VEGETARIAN("non-veg"),
    VEGAN("vegan");

    public final String label;
    Diet(String l) { this.label = l; }

    public static Diet fromLabel(String s) {
        if (s == null) return NONE;
        s = s.toLowerCase();
        if (s.startsWith("none"))  return NONE;
        if (s.startsWith("vegan")) return VEGAN;
        if (s.startsWith("non"))   return NON_VEGETARIAN;
        if (s.startsWith("veg"))   return VEGETARIAN;
        return NONE;
    }
}

