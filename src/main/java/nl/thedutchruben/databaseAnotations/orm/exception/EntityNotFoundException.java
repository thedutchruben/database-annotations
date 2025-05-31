package nl.thedutchruben.databaseAnotations.orm.exception;

/**
 * Thrown when an entity is not found in the database.
 */
public class EntityNotFoundException extends ORMException {
    
    private final Class<?> entityClass;
    private final Object id;
    
    public EntityNotFoundException(Class<?> entityClass, Object id) {
        super("Entity not found: " + entityClass.getSimpleName() + " with id: " + id);
        this.entityClass = entityClass;
        this.id = id;
    }
    
    public Class<?> getEntityClass() {
        return entityClass;
    }
    
    public Object getId() {
        return id;
    }
}
