package by.bsu.dependency.util;

public class DependencyUtil {
    public static String decapitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toLowerCase(input.charAt(0)) + input.substring(1);
    }
}
