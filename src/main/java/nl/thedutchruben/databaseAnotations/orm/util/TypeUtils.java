package nl.thedutchruben.databaseAnotations.orm.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Utility class for type conversions between Java and SQL types.
 */
public class TypeUtils {
    
    private TypeUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if a class represents a basic/primitive type that can be mapped to database columns.
     */
    public static boolean isBasicType(Class<?> type) {
        return type.isPrimitive() ||
               type == String.class ||
               type == Boolean.class ||
               type == Byte.class ||
               type == Short.class ||
               type == Integer.class ||
               type == Long.class ||
               type == Float.class ||
               type == Double.class ||
               type == BigDecimal.class ||
               type == Date.class ||
               type == Time.class ||
               type == Timestamp.class ||
               type == java.util.Date.class ||
               type == LocalDate.class ||
               type == LocalTime.class ||
               type == LocalDateTime.class ||
               type.isEnum();
    }
    
    /**
     * Converts a value to the appropriate type for database storage.
     */
    public static Object convertForDatabase(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        Class<?> valueType = value.getClass();
        
        // If types match, return as-is
        if (targetType.isAssignableFrom(valueType)) {
            return value;
        }
        
        // Handle enum types
        if (targetType.isEnum()) {
            if (value instanceof String) {
                return Enum.valueOf((Class<Enum>) targetType, (String) value);
            }
        } else if (valueType.isEnum()) {
            return ((Enum<?>) value).name();
        }
        
        // Handle date/time conversions
        if (value instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) value;
            if (targetType == Date.class) {
                return new Date(date.getTime());
            } else if (targetType == Time.class) {
                return new Time(date.getTime());
            } else if (targetType == Timestamp.class) {
                return new Timestamp(date.getTime());
            }
        }
        
        // Handle LocalDateTime conversions
        if (value instanceof LocalDateTime) {
            LocalDateTime ldt = (LocalDateTime) value;
            if (targetType == Timestamp.class) {
                return Timestamp.valueOf(ldt);
            }
        }
        
        if (value instanceof LocalDate) {
            LocalDate ld = (LocalDate) value;
            if (targetType == Date.class) {
                return Date.valueOf(ld);
            }
        }
        
        if (value instanceof LocalTime) {
            LocalTime lt = (LocalTime) value;
            if (targetType == Time.class) {
                return Time.valueOf(lt);
            }
        }
        
        // Handle numeric conversions
        if (value instanceof Number) {
            Number num = (Number) value;
            if (targetType == Integer.class || targetType == int.class) {
                return num.intValue();
            } else if (targetType == Long.class || targetType == long.class) {
                return num.longValue();
            } else if (targetType == Double.class || targetType == double.class) {
                return num.doubleValue();
            } else if (targetType == Float.class || targetType == float.class) {
                return num.floatValue();
            } else if (targetType == Short.class || targetType == short.class) {
                return num.shortValue();
            } else if (targetType == Byte.class || targetType == byte.class) {
                return num.byteValue();
            } else if (targetType == BigDecimal.class) {
                return BigDecimal.valueOf(num.doubleValue());
            }
        }
        
        // Handle string conversions
        if (targetType == String.class) {
            return value.toString();
        }
        
        // Handle boolean conversions
        if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue() != 0;
            } else if (value instanceof String) {
                String str = (String) value;
                return "true".equalsIgnoreCase(str) || "1".equals(str) || "yes".equalsIgnoreCase(str);
            }
        }
        
        // If no conversion is possible, return the original value
        return value;
    }
    
    /**
     * Converts a database value to the appropriate Java type.
     */
    public static Object convertFromDatabase(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        return convertForDatabase(value, targetType);
    }
    
    /**
     * Gets the wrapper type for a primitive type.
     */
    public static Class<?> getWrapperType(Class<?> primitiveType) {
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == byte.class) return Byte.class;
        if (primitiveType == char.class) return Character.class;
        if (primitiveType == short.class) return Short.class;
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == double.class) return Double.class;
        return primitiveType;
    }
    
    /**
     * Gets the primitive type for a wrapper type.
     */
    public static Class<?> getPrimitiveType(Class<?> wrapperType) {
        if (wrapperType == Boolean.class) return boolean.class;
        if (wrapperType == Byte.class) return byte.class;
        if (wrapperType == Character.class) return char.class;
        if (wrapperType == Short.class) return short.class;
        if (wrapperType == Integer.class) return int.class;
        if (wrapperType == Long.class) return long.class;
        if (wrapperType == Float.class) return float.class;
        if (wrapperType == Double.class) return double.class;
        return wrapperType;
    }
    
    /**
     * Checks if a type is a collection type.
     */
    public static boolean isCollectionType(Class<?> type) {
        return java.util.Collection.class.isAssignableFrom(type);
    }
    
    /**
     * Gets the default value for a primitive type.
     */
    public static Object getDefaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == char.class) return '\0';
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        return null;
    }
}
