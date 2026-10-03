package com.smartpantry.app;

import java.util.Locale;

/**
 * Normalises ingredient names so simple real-world differences do not break matching:
 * letter case, extra spaces, punctuation and singular/plural ("tomato" vs "Tomatoes").
 */
public class NameUtil {

    public static String normalize(String name) {
        if (name == null) return "";
        String n = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z ]", " ").replaceAll("\\s+", " ").trim();
        int lastSpace = n.lastIndexOf(' ');
        String head = lastSpace >= 0 ? n.substring(0, lastSpace + 1) : "";
        String last = lastSpace >= 0 ? n.substring(lastSpace + 1) : n;
        return head + singular(last);
    }

    private static String singular(String w) {
        if (w.length() > 4 && w.endsWith("ies")) return w.substring(0, w.length() - 3) + "y"; // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);                          // tomatoes -> tomato
        if (w.endsWith("ches") || w.endsWith("shes") || w.endsWith("sses") || w.endsWith("xes"))
            return w.substring(0, w.length() - 2);
        if (w.length() > 3 && w.endsWith("s") && !w.endsWith("ss") && !w.endsWith("us"))
            return w.substring(0, w.length() - 1);                                             // eggs -> egg
        return w;
    }
}
