package dev.twme.pointCylinder.util;

import java.util.ArrayList;
import java.util.List;

public class TabCompleterUtil {
    public static List<String> numberSuggestions(String arg, boolean allowNegative) {

        if (arg.isEmpty()) {
            return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9");
        }
        try {
            if (allowNegative) {
                Integer.parseInt(arg.replaceFirst("-", ""));
            } else {
                Integer.parseInt(arg);
            }
        } catch (NumberFormatException e) {
            return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9");
        }

        List<String> completions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            completions.add(arg + i);
        }
        return completions;
    }
}
