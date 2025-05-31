package nl.thedutchruben.databaseAnotations.orm.annotations;

/**
 * Defines the types of primary key generation strategies.
 */
public enum GenerationType {
    /**
     * Indicates that the persistence provider should pick an appropriate strategy.
     */
    AUTO,
    
    /**
     * Indicates that the persistence provider must use a database identity column.
     */
    IDENTITY,
    
    /**
     * Indicates that the persistence provider must use a database sequence.
     */
    SEQUENCE,
    
    /**
     * Indicates that the persistence provider must use a database table.
     */
    TABLE
}
