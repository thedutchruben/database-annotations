package nl.thedutchruben.databaseAnotations.orm.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Default implementation of the Transaction interface.
 */
public class TransactionImpl implements Transaction {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionImpl.class);
    
    private final Connection connection;
    private boolean active = true;
    
    public TransactionImpl(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public void commit() {
        checkActive();
        try {
            connection.commit();
            active = false;
            logger.debug("Transaction committed");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to commit transaction", e);
        }
    }
    
    @Override
    public void rollback() {
        checkActive();
        try {
            connection.rollback();
            active = false;
            logger.debug("Transaction rolled back");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to rollback transaction", e);
        }
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    private void checkActive() {
        if (!active) {
            throw new RuntimeException("Transaction is not active");
        }
    }
}
