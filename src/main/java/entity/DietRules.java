package entity;

import java.util.List;

public final class DietRules {
    private DietRules() {}

    public static Diet inferDiet(List<String> ingredients) {
        String blob = String.join(" | ", ingredients).toLowerCase();

        String[] nonVeg = {
                "chicken","beef","pork","lamb","mutton","veal","turkey","duck",
                "bacon","ham","prosciutto","chorizo","sausage","meat","steak",
                "mince","anchovy","fish","salmon","tuna","cod","haddock","sardine",
                "prawn","shrimp","crab","lobster","clam","mussel","oyster","octopus","squid"
        };
        for (String t : nonVeg) if (blob.contains(t)) return Diet.NON_VEGETARIAN;

        String[] animalProducts = {
                "egg","eggs","milk","butter","ghee","cheese","yoghurt","yogurt","cream",
                "honey","gelatin","gelatine"
        };
        for (String t : animalProducts) if (blob.contains(t)) return Diet.VEGETARIAN;

        return Diet.VEGAN;
    }
}

