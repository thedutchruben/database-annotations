package nl.thedutchruben.databaseAnotations.orm.annotations;

/**
 * Defines the set of cascadable operations.
 */
public enum CascadeType {
    /**
     * Cascade all operations.
     */
    ALL,
    
    /**
     * Cascade persist operation.
     */
    PERSIST,
    
    /**
     * Cascade merge operation.
     */
    MERGE,
    
    /**
     * Cascade remove operation.
     */
    REMOVE,
    
    /**
     * Cascade refresh operation.
     */
    REFRESH,
    
    /**
     * Cascade detach operation.
     */
    DETACH
}
