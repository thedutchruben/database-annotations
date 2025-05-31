package nl.thedutchruben.databaseAnotations.orm.exception;

/**
 * Thrown when there are issues with entity mapping or configuration.
 */
public class MappingException extends ORMException {
    
    public MappingException(String message) {
        super(message);
    }
    
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static MappingException entityNotRegistered(Class<?> entityClass) {
        return new MappingException("Entity class not registered: " + entityClass.getName());
    }
    
    public static MappingException noPrimaryKey(Class<?> entityClass) {
        return new MappingException("Entity has no primary key defined: " + entityClass.getName());
    }
    
    public static MappingException invalidAnnotation(Class<?> entityClass, String field, String reason) {
        return new MappingException("Invalid annotation on " + entityClass.getName() + "." + field + ": " + reason);
    }
}
