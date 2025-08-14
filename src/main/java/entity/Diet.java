//package entity;
//
//public enum Diet {
//    NONE("none"),
//    VEGETARIAN("veg"),
//    NON_VEGETARIAN("non-veg"),
//    VEGAN("vegan");
//
//    public final String label;
//    Diet(String l) { this.label = l; }
//
//    public static Diet fromLabel(String s) {
//        if (s == null) return NONE;
//        s = s.toLowerCase();
//        if (s.startsWith("none"))  return NONE;
//        if (s.startsWith("vegan")) return VEGAN;
//        if (s.startsWith("non"))   return NON_VEGETARIAN;
//        if (s.startsWith("veg"))   return VEGETARIAN;
//        return NONE;
//    }
//}
//

package entity;

import java.util.Locale;

public enum Diet {
    NONE("none"), VEGETARIAN("veg"), NON_VEGETARIAN("non-veg"), VEGAN("vegan");
    public final String label;
    Diet(String l) { this.label = l; }

    public static Diet fromLabel(String s) {
        if (s == null) return NONE;
        String x = s.toLowerCase(Locale.ROOT);
        if (x.startsWith("none"))  return NONE;
        if (x.startsWith("vegan")) return VEGAN;
        if (x.startsWith("non"))   return NON_VEGETARIAN;
        if (x.startsWith("veg"))   return VEGETARIAN;
        return NONE;
    }
}
