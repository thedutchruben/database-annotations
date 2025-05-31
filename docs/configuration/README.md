# Configuration Guide

## Overview

The Database Annotations ORM framework provides flexible configuration options for different environments and use cases. Configuration is handled through the `Configuration` class and property files.

## Basic Configuration

### Minimal Setup

```java
import nl.thedutchruben.databaseAnotations.orm.core.Configuration;
import nl.thedutchruben.databaseAnotations.orm.session.SessionFactoryImpl;

Configuration config = new Configuration()
    .database("jdbc:h2:mem:testdb", "sa", "")
    .addEntity(User.class);

SessionFactory sessionFactory = new SessionFactoryImpl(config);
```

### Full Configuration

```java
Configuration config = new Configuration()
    // Database connection
    .database("jdbc:mysql://localhost:3306/mydb", "user", "password")
    
    // Entity registration
    .addEntity(User.class)
    .addEntity(Post.class)
    .addEntity(Comment.class)
    
    // SQL settings
    .setProperty("orm.show_sql", "true")
    .setProperty("orm.format_sql", "true")
    .setProperty("orm.use_sql_comments", "true")
    
    // Schema management
    .setProperty("orm.hbm2ddl.auto", "update")
    
    // Performance settings
    .setProperty("orm.performance.monitoring.enabled", "true")
    .setProperty("orm.performance.slow_query_threshold", "1000")
    
    // Connection pooling
    .setProperty("orm.connection.pool.maximum", "20")
    .setProperty("orm.connection.pool.minimum", "5")
    .setProperty("orm.connection.pool.timeout", "30000");
```

## Database Configuration

### Connection URLs

#### MySQL
```java
// Basic connection
.database("jdbc:mysql://localhost:3306/mydb", "user", "password")

// With parameters
.database("jdbc:mysql://localhost:3306/mydb?useSSL=true&requireSSL=true&serverTimezone=UTC", "user", "password")

// With connection pool settings
.database("jdbc:mysql://localhost:3306/mydb?useSSL=true&useUnicode=true&characterEncoding=UTF-8", "user", "password")
```

#### PostgreSQL
```java
// Basic connection
.database("jdbc:postgresql://localhost:5432/mydb", "user", "password")

// With SSL
.database("jdbc:postgresql://localhost:5432/mydb?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", "user", "password")

// With schema
.database("jdbc:postgresql://localhost:5432/mydb?currentSchema=public", "user", "password")
```

#### SQLite
```java
// File database
.database("jdbc:sqlite:/path/to/database.db", "", "")

// In-memory
.database("jdbc:sqlite::memory:", "", "")

// With parameters
.database("jdbc:sqlite:/path/to/database.db?journal_mode=WAL&synchronous=NORMAL", "", "")
```

#### H2
```java
// In-memory
.database("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "")

// File database
.database("jdbc:h2:file:/path/to/database", "sa", "password")

// Server mode
.database("jdbc:h2:tcp://localhost/~/test", "sa", "password")
```

### Connection Pool Configuration

```java
Configuration config = new Configuration()
    .database("jdbc:mysql://localhost:3306/mydb", "user", "password")
    
    // Pool size settings
    .setProperty("orm.connection.pool.maximum", "20")        // Max connections
    .setProperty("orm.connection.pool.minimum", "5")         // Min idle connections
    
    // Timeout settings
    .setProperty("orm.connection.pool.timeout", "30000")     // Connection timeout (ms)
    .setProperty("orm.connection.pool.idle.timeout", "600000") // Idle timeout (ms)
    .setProperty("orm.connection.pool.max.lifetime", "1800000") // Max connection lifetime (ms)
    
    // Validation
    .setProperty("orm.connection.pool.validation.timeout", "5000"); // Validation timeout (ms)
```

## Property Configuration

### Configuration Properties

#### SQL Settings
```properties
# Show SQL statements in logs
orm.show_sql=true

# Format SQL output for readability
orm.format_sql=true

# Add SQL comments for debugging
orm.use_sql_comments=true
```

#### Schema Management
```properties
# Schema generation mode
# Options: none, create, update, validate
orm.hbm2ddl.auto=update

# Default schema and catalog
orm.default_schema=public
orm.default_catalog=myapp
```

#### Performance Settings
```properties
# Enable performance monitoring
orm.performance.monitoring.enabled=true

# Slow query threshold in milliseconds
orm.performance.slow_query_threshold=1000

# Batch operation sizes
orm.batch.size=50
orm.fetch.size=100
```

#### Caching Settings
```properties
# Enable second-level cache (future feature)
orm.cache.use_second_level_cache=false

# Enable query cache (future feature)
orm.cache.use_query_cache=false

# Cache region factory
orm.cache.region.factory_class=
```

#### Logging Settings
```properties
# ORM framework log level
orm.logging.level=INFO

# SQL log level
orm.logging.sql.level=DEBUG
```

### Environment-Specific Configuration

#### Development (orm-dev.properties)
```properties
# Database connection
orm.database.url=jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1
orm.database.username=sa
orm.database.password=
orm.database.driver=org.h2.Driver

# Verbose logging for development
orm.show_sql=true
orm.format_sql=true
orm.use_sql_comments=true

# Auto-create schema for development
orm.hbm2ddl.auto=create

# Performance monitoring enabled
orm.performance.monitoring.enabled=true
orm.performance.slow_query_threshold=100

# Development logging
orm.logging.level=DEBUG
orm.logging.sql.level=DEBUG
```

#### Production (orm-prod.properties)
```properties
# Database connection with environment variables
orm.database.url=jdbc:mysql://prod-db:3306/myapp_prod?useSSL=true&requireSSL=true
orm.database.username=${DB_USERNAME}
orm.database.password=${DB_PASSWORD}
orm.database.driver=com.mysql.cj.jdbc.Driver

# Production connection pool settings
orm.connection.pool.maximum=20
orm.connection.pool.minimum=10
orm.connection.pool.timeout=10000
orm.connection.pool.idle.timeout=300000
orm.connection.pool.max.lifetime=900000

# Minimal logging for production
orm.show_sql=false
orm.format_sql=false
orm.use_sql_comments=false

# Validate schema only
orm.hbm2ddl.auto=validate

# Performance monitoring with higher threshold
orm.performance.monitoring.enabled=true
orm.performance.slow_query_threshold=2000

# Production logging
orm.logging.level=WARN
orm.logging.sql.level=ERROR

# Production optimizations
orm.batch.size=100
orm.fetch.size=200
```

#### Testing (orm-test.properties)
```properties
# In-memory database for tests
orm.database.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
orm.database.username=sa
orm.database.password=
orm.database.driver=org.h2.Driver

# Fast schema recreation for tests
orm.hbm2ddl.auto=create

# Disable performance monitoring for faster tests
orm.performance.monitoring.enabled=false
orm.performance.slow_query_threshold=5000

# Minimal logging for tests
orm.logging.level=WARN
orm.logging.sql.level=INFO
```

## Loading Configuration

### From Properties File

```java
// Load configuration from classpath
Properties props = new Properties();
props.load(getClass().getResourceAsStream("/orm-prod.properties"));

Configuration config = new Configuration();
for (String key : props.stringPropertyNames()) {
    config.setProperty(key, props.getProperty(key));
}

// Set database connection
String url = props.getProperty("orm.database.url");
String username = props.getProperty("orm.database.username");
String password = props.getProperty("orm.database.password");
config.database(url, username, password);
```

### From Environment Variables

```java
Configuration config = new Configuration()
    .database(
        System.getenv("DATABASE_URL"),
        System.getenv("DATABASE_USERNAME"),
        System.getenv("DATABASE_PASSWORD")
    )
    .setProperty("orm.show_sql", System.getenv("ORM_SHOW_SQL"))
    .setProperty("orm.hbm2ddl.auto", System.getenv("ORM_SCHEMA_MODE"));
```

### Profile-Based Configuration

```java
public class ConfigurationFactory {
    
    public static Configuration create(String profile) {
        Configuration config = new Configuration();
        
        switch (profile.toLowerCase()) {
            case "dev":
                return createDevelopmentConfig(config);
            case "test":
                return createTestConfig(config);
            case "prod":
                return createProductionConfig(config);
            default:
                throw new IllegalArgumentException("Unknown profile: " + profile);
        }
    }
    
    private static Configuration createDevelopmentConfig(Configuration config) {
        return config
            .database("jdbc:h2:mem:devdb", "sa", "")
            .setProperty("orm.show_sql", "true")
            .setProperty("orm.hbm2ddl.auto", "create")
            .setProperty("orm.performance.monitoring.enabled", "true");
    }
    
    private static Configuration createProductionConfig(Configuration config) {
        return config
            .database(System.getenv("DATABASE_URL"), 
                     System.getenv("DB_USER"), 
                     System.getenv("DB_PASS"))
            .setProperty("orm.show_sql", "false")
            .setProperty("orm.hbm2ddl.auto", "validate")
            .setProperty("orm.connection.pool.maximum", "20");
    }
}

// Usage
String profile = System.getProperty("app.profile", "dev");
Configuration config = ConfigurationFactory.create(profile);
```

## Advanced Configuration

### Custom DataSource

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// Create custom data source
HikariConfig hikariConfig = new HikariConfig();
hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
hikariConfig.setUsername("user");
hikariConfig.setPassword("password");
hikariConfig.setMaximumPoolSize(20);
hikariConfig.setMinimumIdle(5);
hikariConfig.setConnectionTimeout(30000);

HikariDataSource dataSource = new HikariDataSource(hikariConfig);

// Use custom data source
Configuration config = new Configuration()
    .dataSource(dataSource)
    .addEntity(User.class);
```

### Custom Dialect

```java
import nl.thedutchruben.databaseAnotations.orm.dialect.Dialect;

public class CustomDialect extends Dialect {
    @Override
    public String getLimitString(String sql, int limit) {
        return sql + " LIMIT " + limit;
    }
    
    // Implement other dialect methods...
}

Configuration config = new Configuration()
    .database("jdbc:custom://localhost/mydb", "user", "pass")
    .dialect(new CustomDialect())
    .addEntity(User.class);
```

### Multiple Configurations

```java
// Different configurations for different purposes
Configuration readOnlyConfig = new Configuration()
    .database("jdbc:mysql://read-replica:3306/mydb", "readonly", "pass")
    .addEntity(User.class)
    .setProperty("orm.show_sql", "false");

Configuration writeConfig = new Configuration()
    .database("jdbc:mysql://master:3306/mydb", "writer", "pass")
    .addEntity(User.class)
    .setProperty("orm.show_sql", "true");

SessionFactory readOnlyFactory = new SessionFactoryImpl(readOnlyConfig);
SessionFactory writeFactory = new SessionFactoryImpl(writeConfig);
```

## Configuration Validation

### Built-in Validation

The framework automatically validates:
- Database connection parameters
- Entity class annotations
- Property values and types
- Dialect compatibility

### Custom Validation

```java
public class ConfigurationValidator {
    
    public static void validate(Configuration config) {
        // Validate required entities
        if (config.getEntityClasses().isEmpty()) {
            throw new IllegalStateException("No entities registered");
        }
        
        // Validate database connection
        try (Connection conn = config.getDataSource().getConnection()) {
            // Connection test
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to database", e);
        }
        
        // Validate properties
        String schemaMode = config.getProperty("orm.hbm2ddl.auto");
        if (schemaMode != null && !Arrays.asList("none", "create", "update", "validate").contains(schemaMode)) {
            throw new IllegalArgumentException("Invalid schema mode: " + schemaMode);
        }
    }
}

// Usage
Configuration config = new Configuration()...;
ConfigurationValidator.validate(config);
SessionFactory factory = new SessionFactoryImpl(config);
```

## Configuration Best Practices

### 1. Environment Separation
- Use different property files for dev/test/prod
- Never hardcode production credentials
- Use environment variables for sensitive data

### 2. Connection Pooling
- Set appropriate pool sizes based on application load
- Configure timeouts to prevent connection leaks
- Monitor pool metrics in production

### 3. Schema Management
- Use `create` only for development and testing
- Use `update` carefully in development
- Use `validate` in production
- Use migrations for production schema changes

### 4. Performance Settings
- Enable monitoring in all environments
- Set appropriate slow query thresholds
- Tune batch sizes based on data patterns

### 5. Security
- Use encrypted connections (SSL/TLS)
- Store credentials securely (environment variables, secret managers)
- Limit database user permissions
- Regularly rotate database passwords

### 6. Monitoring
- Enable performance monitoring
- Set up alerts for slow queries
- Monitor connection pool metrics
- Log configuration issues appropriately

## Troubleshooting Configuration

### Common Issues

#### Connection Problems
```java
// Test database connection
try (Connection conn = config.getDataSource().getConnection()) {
    System.out.println("Database connection successful");
    System.out.println("Database: " + conn.getMetaData().getDatabaseProductName());
} catch (SQLException e) {
    System.err.println("Database connection failed: " + e.getMessage());
}
```

#### Entity Registration Issues
```java
// Verify entities are registered
for (Class<?> entityClass : config.getEntityClasses()) {
    System.out.println("Registered entity: " + entityClass.getName());
}
```

#### Property Validation
```java
// Check property values
System.out.println("Show SQL: " + config.isShowSql());
System.out.println("Schema mode: " + config.getHbm2ddlAuto());
System.out.println("Format SQL: " + config.isFormatSql());
```

### Debugging Configuration
```java
// Enable debug logging for configuration
Logger configLogger = LoggerFactory.getLogger(Configuration.class);
configLogger.setLevel(Level.DEBUG);

// Log all properties
Properties props = config.getProperties();
props.forEach((key, value) -> 
    System.out.println(key + " = " + value));
```
