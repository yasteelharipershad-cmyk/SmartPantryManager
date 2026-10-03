package com.smartpantry.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * THE STRICT-MATCHING RULE (Section 2.3 of the brief).
 * A recipe is suggested only if EVERY ingredient it needs is in the pantry
 * in at least the required quantity. One missing or insufficient ingredient = not suggested.
 */
public class RecipeMatcher {

    public static List<Recipe> getSuggested(List<PantryItem> pantry, List<Recipe> allRecipes) {
        // 1. Total the pantry per (normalised name + base unit), so duplicates add up.
        Map<String, Double> stock = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = key(item.getName(), item.getUnit());
            double qty = UnitUtil.toBase(item.getQuantity(), item.getUnit());
            Double existing = stock.get(key);
            stock.put(key, (existing == null ? 0 : existing) + qty);
        }

        // 2. Keep only recipes where every ingredient is covered.
        List<Recipe> suggested = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (canMake(recipe, stock)) suggested.add(recipe);
        }
        return suggested;
    }

    private static boolean canMake(Recipe recipe, Map<String, Double> stock) {
        if (recipe.getIngredients().isEmpty()) return false;
        for (RecipeIngredient ing : recipe.getIngredients()) {
            Double have = stock.get(key(ing.getName(), ing.getUnit()));
            double need = UnitUtil.toBase(ing.getQuantity(), ing.getUnit());
            if (have == null || have + 0.0001 < need) {
                return false; // missing or not enough -> strictly excluded
            }
        }
        return true;
    }

    private static String key(String name, String unit) {
        return NameUtil.normalize(name) + "|" + UnitUtil.baseUnit(unit);
    }
}
