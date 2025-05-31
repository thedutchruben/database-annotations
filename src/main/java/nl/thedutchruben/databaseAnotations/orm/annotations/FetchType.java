package nl.thedutchruben.databaseAnotations.orm.annotations;

/**
 * Defines strategies for fetching data from the database.
 */
public enum FetchType {
    /**
     * Defines that data must be lazily fetched.
     */
    LAZY,
    
    /**
     * Defines that data must be eagerly fetched.
     */
    EAGER
}
