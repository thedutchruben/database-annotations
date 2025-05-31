# Migration Guide

## Overview

The Database Annotations ORM migration system provides version-controlled database schema evolution. Migrations allow you to make incremental changes to your database schema while maintaining data integrity and version history.

## Migration Basics

### What are Migrations?

Migrations are versioned database schema changes that can be applied and rolled back in a controlled manner. Each migration:
- Has a unique version identifier
- Contains forward (up) and backward (down) operations
- Is tracked in a `schema_migrations` table
- Can be applied automatically or manually

### When to Use Migrations

- Adding/removing tables
- Adding/removing columns
- Creating/dropping indexes
- Modifying data types
- Seeding initial data
- Complex schema transformations

## Creating Migrations

### Basic Migration Structure

```java
import nl.thedutchruben.databaseAnotations.orm.migration.Migration;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE INDEX idx_users_email ON users(email)");
        }
    }
    
    @Override
    public void down(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP INDEX idx_users_email");
        }
    }
}
```

### Migration Naming Conventions

Use descriptive, version-prefixed names:
- `001_create_users_table`
- `002_add_user_email_index`
- `003_add_posts_table`
- `004_add_user_id_to_posts`
- `005_modify_user_email_length`

## Migration Types

### Table Creation Migration

```java
public class CreateUsersTableMigration implements Migration {
    
    @Override
    public String getVersion() {
        return "001_create_users_table";
    }
    
    @Override
    public String getDescription() {
        return "Create users table with basic fields";
    }
    
    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    first_name VARCHAR(50),
                    last_name VARCHAR(50),
                    age INTEGER,
                    active BOOLEAN NOT NULL DEFAULT true,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);
        }
    }
    
    @Override
    public void down(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE users");
        }
    }
}
```

### Column Addition Migration

```java
public class AddUserBirthDateMigration implements Migration {
    
    @Override
    public String getVersion() {
        return "006_add_user_birth_date";
    }
    
    @Override
    public String getDescription() {
        return "Add birth_date column to users table";
    }
    
    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE users ADD COLUMN birth_date DATE");
        }
    }
    
    @Override
    public void down(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE users DROP COLUMN birth_date");
        }
    }
}
```

### Index Creation Migration

```java
public class AddPerformanceIndexesMigration implements Migration {
    
    @Override
    public String getVersion() {
        return "007_add_performance_indexes";
    }
    
    @Override
    public String getDescription() {
        return "Add performance indexes for common queries";
    }
    
    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Single column indexes
            stmt.execute("CREATE INDEX idx_users_username ON users(username)");
            stmt.execute("CREATE INDEX idx_users_email ON users(email)");
            stmt.execute("CREATE INDEX idx_posts_user_id ON posts(user_id)");
            stmt.execute("CREATE INDEX idx_posts_created_at ON posts(created_at)");
            
            // Composite indexes
            stmt.execute("CREATE INDEX idx_posts_user_created ON posts(user_id, created_at)");
            stmt.execute("CREATE INDEX idx_comments_post_created ON comments(post_id, created_at)");
        }
    }
    
    @Override
    public void down(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP INDEX idx_users_username");
            stmt.execute("DROP INDEX idx_users_email");
            stmt.execute("DROP INDEX idx_posts_user_id");
            stmt.execute("DROP INDEX idx_posts_created_at");
            stmt.execute("DROP INDEX idx_posts_user_created");
            stmt.execute("DROP INDEX idx_comments_post_created");
        }
    }
}
```

### Data Migration

```java
public class SeedInitialDataMigration implements Migration {
    
    @Override
    public String getVersion() {
        return "008_seed_initial_data";
    }
    
    @Override
    public String getDescription() {
        return "Seed initial admin user and default categories";
    }
    
    @Override
    public void up(Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO users (username, email, first_name, last_name, active) VALUES (?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, "admin");
            stmt.setString(2, "admin@example.com");
            stmt.setString(3, "Admin");
            stmt.setString(4, "User");
            stmt.setBoolean(5, true);
            stmt.executeUpdate();
        }
        
        // Add default categories
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO categories (name, description) VALUES " +
                        "('General', 'General discussion'), " +
                        "('Technology', 'Technology related posts'), " +
                        "('News', 'News and updates')");
        }
    }
    
    @Override
    public void down(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM users WHERE username = 'admin'");
            stmt.execute("DELETE FROM categories WHERE name IN ('General', 'Technology', 'News')");
        }
    }
}
```

### Complex Schema Change Migration

```java
public class ModifyUserEmailLengthMigration implements Migration {
    
    @Override
    public String getVersion() {
        return "009_modify_user_email_length";
    }
    
    @Override
    public String getDescription() {
        return "Increase user email column length from 100 to 255 characters";
    }
    
    @Override
    public void up(Connection connection) throws SQLException {
        // Check for existing data that might be too long
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE LENGTH(email) > 255")) {
            
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Found emails longer than 255 characters. Please clean data first.");
            }
        }
        
        // Modify column
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE users MODIFY COLUMN email VARCHAR(255) NOT NULL");
        }
    }
    
    @Override
    public void down(Connection connection) throws SQLException {
        // Check for data that would be truncated
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE LENGTH(email) > 100")) {
            
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Found emails longer than 100 characters. Cannot safely rollback.");
            }
        }
        
        // Revert column change
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE users MODIFY COLUMN email VARCHAR(100) NOT NULL");
        }
    }
}
```

## Running Migrations

### Using MigrationManager

```java
import nl.thedutchruben.databaseAnotations.orm.migration.MigrationManager;

// Create migration manager
MigrationManager migrationManager = new MigrationManager(config.getDataSource());

// Add migrations in order
migrationManager.addMigration(new CreateUsersTableMigration());
migrationManager.addMigration(new AddUserEmailIndexMigration());
migrationManager.addMigration(new AddPerformanceIndexesMigration());
migrationManager.addMigration(new SeedInitialDataMigration());

// Execute all pending migrations
migrationManager.migrate();
```

### Complete Migration Setup

```java
public class DatabaseMigrator {
    
    private final MigrationManager migrationManager;
    
    public DatabaseMigrator(DataSource dataSource) {
        this.migrationManager = new MigrationManager(dataSource);
        registerMigrations();
    }
    
    private void registerMigrations() {
        // Register migrations in chronological order
        migrationManager.addMigration(new CreateUsersTableMigration());
        migrationManager.addMigration(new CreatePostsTableMigration());
        migrationManager.addMigration(new CreateCommentsTableMigration());
        migrationManager.addMigration(new AddUserEmailIndexMigration());
        migrationManager.addMigration(new AddPerformanceIndexesMigration());
        migrationManager.addMigration(new SeedInitialDataMigration());
    }
    
    public void migrate() {
        System.out.println("Starting database migration...");
        migrationManager.migrate();
        System.out.println("Database migration completed successfully!");
    }
    
    public void checkMigrationStatus() {
        // Implementation would check which migrations are applied
        System.out.println("Checking migration status...");
    }
}

// Usage
DatabaseMigrator migrator = new DatabaseMigrator(config.getDataSource());
migrator.migrate();
```

### Integration with Application Startup

```java
public class Application {
    
    public static void main(String[] args) {
        // Load configuration
        Configuration config = loadConfiguration();
        
        // Run migrations before starting application
        runMigrations(config.getDataSource());
        
        // Start application
        SessionFactory sessionFactory = new SessionFactoryImpl(config);
        startApplication(sessionFactory);
    }
    
    private static void runMigrations(DataSource dataSource) {
        try {
            DatabaseMigrator migrator = new DatabaseMigrator(dataSource);
            migrator.migrate();
        } catch (Exception e) {
            System.err.println("Migration failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
```

## Migration Best Practices

### 1. Version Management

```java
// Use consistent version numbering
public class Migration001CreateUsers implements Migration {
    @Override
    public String getVersion() {
        return "001_create_users_table";
    }
}

public class Migration002AddEmailIndex implements Migration {
    @Override
    public String getVersion() {
        return "002_add_email_index";
    }
}
```

### 2. Atomic Operations

```java
@Override
public void up(Connection connection) throws SQLException {
    // Use transactions for atomic operations
    connection.setAutoCommit(false);
    
    try {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE temp_users AS SELECT * FROM users");
            stmt.execute("DROP TABLE users");
            stmt.execute("CREATE TABLE users (/* new schema */)");
            stmt.execute("INSERT INTO users SELECT /* mapped columns */ FROM temp_users");
            stmt.execute("DROP TABLE temp_users");
        }
        
        connection.commit();
    } catch (SQLException e) {
        connection.rollback();
        throw e;
    } finally {
        connection.setAutoCommit(true);
    }
}
```

### 3. Data Validation

```java
@Override
public void up(Connection connection) throws SQLException {
    // Validate data before making changes
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE email IS NULL")) {
        
        if (rs.next() && rs.getInt(1) > 0) {
            throw new SQLException("Cannot add NOT NULL constraint: found null emails");
        }
    }
    
    // Apply the change
    try (Statement stmt = connection.createStatement()) {
        stmt.execute("ALTER TABLE users MODIFY COLUMN email VARCHAR(100) NOT NULL");
    }
}
```

### 4. Rollback Safety

```java
@Override
public void down(Connection connection) throws SQLException {
    // Check if rollback is safe
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM posts WHERE user_id IS NULL")) {
        
        if (rs.next() && rs.getInt(1) > 0) {
            throw new SQLException("Cannot safely rollback: found orphaned posts");
        }
    }
    
    // Perform rollback
    try (Statement stmt = connection.createStatement()) {
        stmt.execute("ALTER TABLE posts DROP FOREIGN KEY fk_posts_user_id");
    }
}
```

### 5. Environment-Specific Migrations

```java
public class ConditionalMigration implements Migration {
    
    @Override
    public void up(Connection connection) throws SQLException {
        String environment = System.getProperty("app.environment", "dev");
        
        if ("prod".equals(environment)) {
            // Production-specific changes
            createProductionIndexes(connection);
        } else {
            // Development/test changes
            createDevelopmentData(connection);
        }
    }
    
    private void createProductionIndexes(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE INDEX idx_users_last_login ON users(last_login_at)");
        }
    }
    
    private void createDevelopmentData(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO users (username, email) VALUES ('test', 'test@example.com')");
        }
    }
}
```

## Advanced Migration Patterns

### Large Data Migrations

```java
public class LargeDataMigration implements Migration {
    
    @Override
    public void up(Connection connection) throws SQLException {
        // Process data in batches to avoid memory issues
        int batchSize = 1000;
        int offset = 0;
        
        try (PreparedStatement selectStmt = connection.prepareStatement(
                "SELECT id, old_field FROM large_table LIMIT ? OFFSET ?");
             PreparedStatement updateStmt = connection.prepareStatement(
                "UPDATE large_table SET new_field = ? WHERE id = ?")) {
            
            while (true) {
                selectStmt.setInt(1, batchSize);
                selectStmt.setInt(2, offset);
                
                try (ResultSet rs = selectStmt.executeQuery()) {
                    int count = 0;
                    
                    while (rs.next()) {
                        String transformedValue = transformData(rs.getString("old_field"));
                        updateStmt.setString(1, transformedValue);
                        updateStmt.setLong(2, rs.getLong("id"));
                        updateStmt.addBatch();
                        count++;
                    }
                    
                    if (count == 0) break; // No more data
                    
                    updateStmt.executeBatch();
                    offset += batchSize;
                    
                    System.out.println("Processed " + offset + " records");
                }
            }
        }
    }
    
    private String transformData(String oldValue) {
        // Data transformation logic
        return oldValue != null ? oldValue.toUpperCase() : null;
    }
}
```

### Conditional Migrations

```java
public class ConditionalIndexMigration implements Migration {
    
    @Override
    public void up(Connection connection) throws SQLException {
        // Check if index already exists
        if (!indexExists(connection, "users", "idx_users_email")) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE INDEX idx_users_email ON users(email)");
            }
        }
    }
    
    private boolean indexExists(Connection connection, String table, String indexName) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM information_schema.statistics " +
                "WHERE table_name = ? AND index_name = ?")) {
            
            stmt.setString(1, table);
            stmt.setString(2, indexName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
```

## Migration Troubleshooting

### Common Issues

#### 1. Migration Order Problems
```java
// Wrong: Dependencies not in order
migrationManager.addMigration(new AddForeignKeyMigration()); // Requires users table
migrationManager.addMigration(new CreateUsersTableMigration()); // Creates users table

// Correct: Dependencies in order
migrationManager.addMigration(new CreateUsersTableMigration()); // Creates users table
migrationManager.addMigration(new AddForeignKeyMigration()); // Requires users table
```

#### 2. Failed Migration Recovery
```java
public class RecoveryMigration implements Migration {
    
    @Override
    public void up(Connection connection) throws SQLException {
        // Check if previous migration left system in inconsistent state
        if (isInconsistentState(connection)) {
            repairInconsistentState(connection);
        }
        
        // Proceed with migration
        performMigration(connection);
    }
    
    private boolean isInconsistentState(Connection connection) throws SQLException {
        // Check for specific inconsistency indicators
        return false;
    }
}
```

### Debugging Migrations

```java
public class DebuggableMigration implements Migration {
    
    @Override
    public void up(Connection connection) throws SQLException {
        System.out.println("Starting migration: " + getVersion());
        
        try {
            // Migration steps with logging
            System.out.println("Step 1: Creating table...");
            createTable(connection);
            
            System.out.println("Step 2: Adding indexes...");
            addIndexes(connection);
            
            System.out.println("Migration completed successfully");
            
        } catch (SQLException e) {
            System.err.println("Migration failed at step: " + e.getMessage());
            throw e;
        }
    }
}
```

## Migration Testing

### Test Migrations

```java
@Test
public void testUserTableMigration() throws SQLException {
    // Setup test database
    Connection connection = getTestConnection();
    
    // Apply migration
    CreateUsersTableMigration migration = new CreateUsersTableMigration();
    migration.up(connection);
    
    // Verify table exists and has correct structure
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery("DESCRIBE users")) {
        
        Set<String> columns = new HashSet<>();
        while (rs.next()) {
            columns.add(rs.getString("Field"));
        }
        
        assertTrue(columns.contains("id"));
        assertTrue(columns.contains("username"));
        assertTrue(columns.contains("email"));
    }
    
    // Test rollback
    migration.down(connection);
    
    // Verify table is dropped
    assertThrows(SQLException.class, () -> {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeQuery("SELECT COUNT(*) FROM users");
        }
    });
}
```

This comprehensive migration guide provides everything needed to implement and manage database schema evolution using the Database Annotations ORM framework.
