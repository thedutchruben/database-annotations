package nl.thedutchruben.databaseAnotations.orm.util;

/**
 * Utility class for common string operations in the ORM framework.
 */
public class StringUtils {
    
    private StringUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if a string is null or empty.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Checks if a string is not null and not empty.
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * Converts camelCase to snake_case.
     */
    public static String camelToSnake(String camelCase) {
        if (isEmpty(camelCase)) {
            return camelCase;
        }
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < camelCase.length(); i++) {
            char current = camelCase.charAt(i);
            
            if (Character.isUpperCase(current)) {
                // Add underscore before uppercase letter if:
                // - Not the first character AND
                // - Previous character is lowercase OR
                // - Previous character is uppercase but next character is lowercase (end of acronym)
                if (i > 0) {
                    char previous = camelCase.charAt(i - 1);
                    boolean nextIsLower = (i + 1 < camelCase.length() && Character.isLowerCase(camelCase.charAt(i + 1)));
                    
                    if (Character.isLowerCase(previous) || (Character.isUpperCase(previous) && nextIsLower)) {
                        result.append('_');
                    }
                }
                result.append(Character.toLowerCase(current));
            } else {
                result.append(current);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Converts snake_case to camelCase.
     */
    public static String snakeToCamel(String snakeCase) {
        if (isEmpty(snakeCase)) {
            return snakeCase;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (int i = 0; i < snakeCase.length(); i++) {
            char c = snakeCase.charAt(i);
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(c);
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * Capitalizes the first letter of a string.
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * Uncapitalizes the first letter of a string.
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * Joins array elements with a delimiter.
     */
    public static String join(String[] elements, String delimiter) {
        if (elements == null || elements.length == 0) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            if (i > 0) {
                result.append(delimiter);
            }
            result.append(elements[i]);
        }
        return result.toString();
    }
    
    /**
     * Repeats a string n times.
     */
    public static String repeat(String str, int count) {
        if (str == null || count <= 0) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(str);
        }
        return result.toString();
    }
}
