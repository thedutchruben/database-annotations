package nl.thedutchruben.databaseAnotations.orm.migration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for database migrations.
 */
public interface Migration {
    
    /**
     * Gets the migration version identifier.
     */
    String getVersion();
    
    /**
     * Gets a description of what this migration does.
     */
    String getDescription();
    
    /**
     * Applies the migration (schema changes).
     */
    void up(Connection connection) throws SQLException;
    
    /**
     * Reverts the migration (optional).
     */
    default void down(Connection connection) throws SQLException {
        throw new UnsupportedOperationException("Migration rollback not implemented");
    }
}
