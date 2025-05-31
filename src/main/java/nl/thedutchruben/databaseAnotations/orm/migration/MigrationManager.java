package nl.thedutchruben.databaseAnotations.orm.migration;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple database migration manager.
 */
public class MigrationManager {
    
    private final DataSource dataSource;
    private final List<Migration> migrations = new ArrayList<>();
    
    public MigrationManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void addMigration(Migration migration) {
        migrations.add(migration);
    }
    
    public void migrate() {
        try {
            ensureMigrationTable();
            
            for (Migration migration : migrations) {
                if (!isMigrationApplied(migration.getVersion())) {
                    applyMigration(migration);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Migration failed", e);
        }
    }
    
    private void ensureMigrationTable() throws SQLException {
        String createTable = """
            CREATE TABLE IF NOT EXISTS schema_migrations (
                version VARCHAR(255) PRIMARY KEY,
                applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
        }
    }
    
    private boolean isMigrationApplied(String version) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM schema_migrations WHERE version = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setString(1, version);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    private void applyMigration(Migration migration) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Apply migration
                migration.up(conn);
                
                // Record migration
                String recordSql = "INSERT INTO schema_migrations (version) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(recordSql)) {
                    stmt.setString(1, migration.getVersion());
                    stmt.executeUpdate();
                }
                
                conn.commit();
                System.out.println("Applied migration: " + migration.getVersion());
                
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Failed to apply migration: " + migration.getVersion(), e);
            }
        }
    }
}
