package nl.thedutchruben.databaseAnotations.orm.exception;

/**
 * Thrown when there are persistence/database operation errors.
 */
public class PersistenceException extends ORMException {
    
    public PersistenceException(String message) {
        super(message);
    }
    
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PersistenceException saveFailed(Object entity, Throwable cause) {
        return new PersistenceException("Failed to save entity: " + entity.getClass().getSimpleName(), cause);
    }
    
    public static PersistenceException updateFailed(Object entity, Throwable cause) {
        return new PersistenceException("Failed to update entity: " + entity.getClass().getSimpleName(), cause);
    }
    
    public static PersistenceException deleteFailed(Object entity, Throwable cause) {
        return new PersistenceException("Failed to delete entity: " + entity.getClass().getSimpleName(), cause);
    }
    
    public static PersistenceException connectionFailed(Throwable cause) {
        return new PersistenceException("Failed to obtain database connection", cause);
    }
    
    public static PersistenceException schemaGenerationFailed(Throwable cause) {
        return new PersistenceException("Failed to generate database schema", cause);
    }
}
