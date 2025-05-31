package nl.thedutchruben.databaseAnotations.orm.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Example migration that adds an index to the users table.
 */
public class AddUserEmailIndexMigration implements Migration {
    
    @Override
    public String getVersion() {
        return "001_add_user_email_index";
    }
    
    @Override
    public String getDescription() {
        return "Add index on users.email column for better query performance";
    }
    
    @Override
    public void up(Connection connection) throws SQLException {
        String createIndex = "CREATE INDEX idx_users_email ON users(email)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createIndex);
        }
    }
    
    @Override
    public void down(Connection connection) throws SQLException {
        String dropIndex = "DROP INDEX idx_users_email";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(dropIndex);
        }
    }
}
