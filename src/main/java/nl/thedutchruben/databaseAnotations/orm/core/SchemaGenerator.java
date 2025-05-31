package nl.thedutchruben.databaseAnotations.orm.core;

import nl.thedutchruben.databaseAnotations.orm.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for generating database schema from entity metadata.
 */
public class SchemaGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(SchemaGenerator.class);
    
    private final DataSource dataSource;
    private final Dialect dialect;
    private final Map<Class<?>, EntityMetadata> entityMetadataMap;
    
    public SchemaGenerator(DataSource dataSource, Dialect dialect, Map<Class<?>, EntityMetadata> entityMetadataMap) {
        this.dataSource = dataSource;
        this.dialect = dialect;
        this.entityMetadataMap = entityMetadataMap;
    }
    
    /**
     * Creates all tables for registered entities.
     */
    public void createSchema() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            for (EntityMetadata metadata : entityMetadataMap.values()) {
                String createTableSql = generateCreateTableSql(metadata);
                logger.info("Creating table: {}", metadata.getTableName());
                logger.debug("SQL: {}", createTableSql);
                statement.execute(createTableSql);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create schema", e);
        }
    }
    
    /**
     * Drops all tables for registered entities.
     */
    public void dropSchema() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            for (EntityMetadata metadata : entityMetadataMap.values()) {
                String dropTableSql = generateDropTableSql(metadata);
                logger.info("Dropping table: {}", metadata.getTableName());
                logger.debug("SQL: {}", dropTableSql);
                statement.execute(dropTableSql);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop schema", e);
        }
    }
    
    /**
     * Drops and recreates all tables.
     */
    public void recreateSchema() {
        dropSchema();
        createSchema();
    }
    
    private String generateCreateTableSql(EntityMetadata metadata) {
        StringBuilder sql = new StringBuilder();
        sql.append(dialect.getCreateTableString())
           .append(" ")
           .append(metadata.getFullTableName())
           .append(" (");
        
        List<String> columnDefinitions = new ArrayList<>();
        List<String> constraints = new ArrayList<>();
        
        // Generate column definitions for basic columns
        for (ColumnMetadata column : metadata.getColumns().values()) {
            String columnDef = generateColumnDefinition(column);
            columnDefinitions.add(columnDef);
            
            // Generate constraints
            if (column.isPrimaryKey()) {
                constraints.add(dialect.getPrimaryKeyString() + " (" + column.getColumnName() + ")");
            }
            
            if (column.isUnique() && !column.isPrimaryKey()) {
                constraints.add(dialect.getUniqueString() + " (" + column.getColumnName() + ")");
            }
        }
        
        // Generate foreign key columns for ManyToOne and OneToOne relationships
        for (RelationshipMetadata relationship : metadata.getRelationships().values()) {
            if (relationship.getRelationshipType() == RelationshipType.MANY_TO_ONE ||
                relationship.getRelationshipType() == RelationshipType.ONE_TO_ONE) {
                String foreignKeyColumn = generateForeignKeyColumnDefinition(relationship);
                if (foreignKeyColumn != null) {
                    columnDefinitions.add(foreignKeyColumn);
                }
            }
        }
        
        // Add column definitions
        sql.append(String.join(", ", columnDefinitions));
        
        // Add constraints
        if (!constraints.isEmpty()) {
            sql.append(", ").append(String.join(", ", constraints));
        }
        
        sql.append(")");
        
        return sql.toString();
    }
    
    private String generateColumnDefinition(ColumnMetadata column) {
        StringBuilder columnDef = new StringBuilder();
        
        columnDef.append(column.getColumnName())
                 .append(" ")
                 .append(dialect.getColumnType(column));
        
        // Add identity/auto-increment for primary keys
        if (column.isPrimaryKey() && column.getGenerationType() != null) {
            String identityString = dialect.getIdentityColumnString();
            if (!identityString.isEmpty()) {
                columnDef.append(" ").append(identityString);
            }
        }
        
        // Add NOT NULL constraint
        if (!column.isNullable()) {
            columnDef.append(" ").append(dialect.getNotNullString());
        }
        
        return columnDef.toString();
    }
    
    /**
     * Generates a foreign key column definition for ManyToOne and OneToOne relationships.
     */
    private String generateForeignKeyColumnDefinition(RelationshipMetadata relationship) {
        String joinColumn = relationship.getJoinColumn();
        if (joinColumn == null || joinColumn.isEmpty()) {
            return null;
        }
        
        StringBuilder columnDef = new StringBuilder();
        columnDef.append(joinColumn);
        
        // Determine the foreign key column type based on the target entity's primary key
        Class<?> targetEntityClass = relationship.getTargetEntity();
        EntityMetadata targetMetadata = entityMetadataMap.get(targetEntityClass);
        if (targetMetadata != null && targetMetadata.getPrimaryKey() != null) {
            ColumnMetadata targetPK = targetMetadata.getPrimaryKey();
            columnDef.append(" ").append(dialect.getColumnType(targetPK));
        } else {
            // Default to BIGINT for foreign keys
            columnDef.append(" BIGINT");
        }
        
        // Add NOT NULL constraint if the relationship is not optional
        if (!relationship.isOptional()) {
            columnDef.append(" ").append(dialect.getNotNullString());
        }
        
        return columnDef.toString();
    }
    
    private String generateDropTableSql(EntityMetadata metadata) {
        return dialect.getDropTableString() + " " + metadata.getFullTableName();
    }
}
