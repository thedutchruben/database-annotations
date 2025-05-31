package nl.thedutchruben.databaseAnotations.orm.exception;

/**
 * Thrown when there are transaction-related errors.
 */
public class TransactionException extends ORMException {
    
    public TransactionException(String message) {
        super(message);
    }
    
    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static TransactionException notActive() {
        return new TransactionException("No active transaction");
    }
    
    public static TransactionException alreadyActive() {
        return new TransactionException("Transaction already active");
    }
    
    public static TransactionException commitFailed(Throwable cause) {
        return new TransactionException("Failed to commit transaction", cause);
    }
    
    public static TransactionException rollbackFailed(Throwable cause) {
        return new TransactionException("Failed to rollback transaction", cause);
    }
}
