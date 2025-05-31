package nl.thedutchruben.databaseAnotations.orm.core;

import nl.thedutchruben.databaseAnotations.orm.dialect.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Configuration class for the ORM framework.
 */
public class Configuration {
    
    private DataSource dataSource;
    private Dialect dialect;
    private List<Class<?>> entityClasses = new ArrayList<>();
    private Properties properties = new Properties();
    
    public Configuration() {
        // Set default properties
        properties.setProperty("orm.show_sql", "false");
        properties.setProperty("orm.format_sql", "false");
        properties.setProperty("orm.hbm2ddl.auto", "none");
    }
    
    /**
     * Configures the database connection.
     */
    public Configuration database(String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        
        // Set connection pool properties
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        this.dataSource = new HikariDataSource(config);
        
        // Auto-detect dialect based on URL
        autoDetectDialect(url);
        
        return this;
    }
    
    /**
     * Sets a custom data source.
     */
    public Configuration dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }
    
    /**
     * Sets the database dialect.
     */
    public Configuration dialect(Dialect dialect) {
        this.dialect = dialect;
        return this;
    }
    
    /**
     * Adds an entity class to be managed by the ORM.
     */
    public Configuration addEntity(Class<?> entityClass) {
        if (!entityClasses.contains(entityClass)) {
            entityClasses.add(entityClass);
        }
        return this;
    }
    
    /**
     * Sets a configuration property.
     */
    public Configuration setProperty(String key, String value) {
        properties.setProperty(key, value);
        return this;
    }
    
    /**
     * Gets a configuration property.
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    private void autoDetectDialect(String url) {
        if (url.startsWith("jdbc:mysql:")) {
            this.dialect = new MySQLDialect();
        } else if (url.startsWith("jdbc:postgresql:")) {
            this.dialect = new PostgreSQLDialect();
        } else if (url.startsWith("jdbc:sqlite:")) {
            this.dialect = new SQLiteDialect();
        } else if (url.startsWith("jdbc:h2:")) {
            this.dialect = new H2Dialect();
        } else {
            throw new IllegalArgumentException("Unsupported database URL: " + url);
        }
    }
    
    // Getters
    public DataSource getDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource not configured");
        }
        return dataSource;
    }
    
    public Dialect getDialect() {
        if (dialect == null) {
            throw new IllegalStateException("Dialect not configured");
        }
        return dialect;
    }
    
    public List<Class<?>> getEntityClasses() {
        return entityClasses;
    }
    
    public Properties getProperties() {
        return properties;
    }
    
    public boolean isShowSql() {
        return Boolean.parseBoolean(properties.getProperty("orm.show_sql", "false"));
    }
    
    public boolean isFormatSql() {
        return Boolean.parseBoolean(properties.getProperty("orm.format_sql", "false"));
    }
    
    public String getHbm2ddlAuto() {
        return properties.getProperty("orm.hbm2ddl.auto", "none");
    }
}
