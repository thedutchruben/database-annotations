package nl.thedutchruben.databaseAnotations.orm.session;

/**
 * Interface for database transaction management.
 */
public interface Transaction {
    
    /**
     * Commits the current transaction.
     */
    void commit();
    
    /**
     * Rolls back the current transaction.
     */
    void rollback();
    
    /**
     * Checks if the transaction is active.
     */
    boolean isActive();
}
