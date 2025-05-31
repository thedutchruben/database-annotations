package nl.thedutchruben.databaseAnotations.orm.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for reflection operations.
 */
public class ReflectionUtils {
    
    private ReflectionUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets all fields from a class, including inherited fields.
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        
        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        
        return fields;
    }
    
    /**
     * Gets all fields that have a specific annotation.
     */
    public static List<Field> getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Field> annotatedFields = new ArrayList<>();
        
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(annotationClass)) {
                annotatedFields.add(field);
            }
        }
        
        return annotatedFields;
    }
    
    /**
     * Gets the first field with a specific annotation.
     */
    public static Field getFieldWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(annotationClass)) {
                return field;
            }
        }
        return null;
    }
    
    /**
     * Gets a field by name, including inherited fields.
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        
        while (currentClass != null && currentClass != Object.class) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        
        return null;
    }
    
    /**
     * Sets a field value, making it accessible if necessary.
     */
    public static void setFieldValue(Object target, Field field, Object value) {
        try {
            boolean wasAccessible = field.isAccessible();
            if (!wasAccessible) {
                field.setAccessible(true);
            }
            
            field.set(target, value);
            
            if (!wasAccessible) {
                field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set field value: " + field.getName(), e);
        }
    }
    
    /**
     * Gets a field value, making it accessible if necessary.
     */
    public static Object getFieldValue(Object target, Field field) {
        try {
            boolean wasAccessible = field.isAccessible();
            if (!wasAccessible) {
                field.setAccessible(true);
            }
            
            Object value = field.get(target);
            
            if (!wasAccessible) {
                field.setAccessible(false);
            }
            
            return value;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get field value: " + field.getName(), e);
        }
    }
    
    /**
     * Creates a new instance of a class using the default constructor.
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of: " + clazz.getName(), e);
        }
    }
    
    /**
     * Checks if a class has a default (no-argument) constructor.
     */
    public static boolean hasDefaultConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor() != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
    
    /**
     * Gets all methods from a class, including inherited methods.
     */
    public static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        Class<?> currentClass = clazz;
        
        while (currentClass != null && currentClass != Object.class) {
            methods.addAll(Arrays.asList(currentClass.getDeclaredMethods()));
            currentClass = currentClass.getSuperclass();
        }
        
        return methods;
    }
    
    /**
     * Checks if a field is static.
     */
    public static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }
    
    /**
     * Checks if a field is final.
     */
    public static boolean isFinal(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }
    
    /**
     * Checks if a field is transient.
     */
    public static boolean isTransient(Field field) {
        return Modifier.isTransient(field.getModifiers());
    }
    
    /**
     * Gets the generic type of a field if it's a parameterized type.
     */
    public static Class<?> getGenericType(Field field) {
        try {
            if (field.getGenericType() instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType paramType = 
                    (java.lang.reflect.ParameterizedType) field.getGenericType();
                java.lang.reflect.Type[] actualTypes = paramType.getActualTypeArguments();
                if (actualTypes.length > 0) {
                    return (Class<?>) actualTypes[0];
                }
            }
            return field.getType();
        } catch (Exception e) {
            return field.getType();
        }
    }
}
