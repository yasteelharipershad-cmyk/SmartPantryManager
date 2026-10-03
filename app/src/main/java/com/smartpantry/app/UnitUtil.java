package com.smartpantry.app;

import java.util.Locale;

/**
 * Converts the different units a user may type into one base unit per kind of measurement:
 * weight -> grams ("g"), volume -> millilitres ("ml"), everything else -> pieces ("pcs").
 * This is what makes "0.5 kg flour" in the pantry satisfy "200 g flour" in a recipe.
 */
public class UnitUtil {

    public static String baseUnit(String unit) {
        switch (clean(unit)) {
            case "kg": case "g": case "mg": case "lb": case "oz":
                return "g";
            case "l": case "ml": case "tsp": case "tbsp": case "cup": case "cups":
                return "ml";
            default:
                return "pcs";
        }
    }

    public static double toBase(double quantity, String unit) {
        switch (clean(unit)) {
            case "kg":   return quantity * 1000;
            case "mg":   return quantity / 1000;
            case "lb":   return quantity * 453.6;
            case "oz":   return quantity * 28.35;
            case "l":    return quantity * 1000;
            case "tsp":  return quantity * 5;
            case "tbsp": return quantity * 15;
            case "cup": case "cups": return quantity * 250;
            default:     return quantity; // g, ml, pcs
        }
    }

    /** Prints 3.0 as "3" and 2.5 as "2.5". */
    public static String format(double q) {
        return q == Math.floor(q) ? String.valueOf((long) q) : String.valueOf(q);
    }

    private static String clean(String unit) {
        return unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
    }
}
