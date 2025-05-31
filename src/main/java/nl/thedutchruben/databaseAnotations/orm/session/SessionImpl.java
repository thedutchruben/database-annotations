package nl.thedutchruben.databaseAnotations.orm.session;

import nl.thedutchruben.databaseAnotations.orm.core.ColumnMetadata;
import nl.thedutchruben.databaseAnotations.orm.core.EntityMetadata;
import nl.thedutchruben.databaseAnotations.orm.core.RelationshipMetadata;
import nl.thedutchruben.databaseAnotations.orm.core.RelationshipType;
import nl.thedutchruben.databaseAnotations.orm.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the Session interface.
 */
public class SessionImpl implements Session {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionImpl.class);
    
    private final DataSource dataSource;
    private final Dialect dialect;
    private final Map<Class<?>, EntityMetadata> entityMetadataMap;
    private final Map<Object, Object> entityCache = new HashMap<>();
    private Connection connection;
    private TransactionImpl currentTransaction;
    private boolean open = true;
    
    public SessionImpl(DataSource dataSource, Dialect dialect, Map<Class<?>, EntityMetadata> entityMetadataMap) {
        this.dataSource = dataSource;
        this.dialect = dialect;
        this.entityMetadataMap = entityMetadataMap;
    }
    
    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = dataSource.getConnection();
            connection.setAutoCommit(currentTransaction == null);
        }
        return connection;
    }
    
    @Override
    public <T> T save(T entity) {
        try {
            EntityMetadata metadata = getEntityMetadata(entity.getClass());
            String sql = buildInsertSql(metadata);
            
            try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setInsertParameters(stmt, entity, metadata);
                
                logger.debug("Executing SQL: {}", sql);
                int result = stmt.executeUpdate();
                
                if (result > 0) {
                    // Handle generated keys
                    ColumnMetadata pkColumn = metadata.getPrimaryKey();
                    if (pkColumn != null && pkColumn.getGenerationType() != null) {
                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                Object generatedId = rs.getObject(1);
                                pkColumn.setValue(entity, generatedId);
                            }
                        }
                    }
                    
                    // Cache the entity
                    Object id = pkColumn != null ? pkColumn.getValue(entity) : entity;
                    entityCache.put(id, entity);
                }
                
                return entity;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save entity", e);
        }
    }
    
    @Override
    public <T> T update(T entity) {
        try {
            EntityMetadata metadata = getEntityMetadata(entity.getClass());
            String sql = buildUpdateSql(metadata);
            
            try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                setUpdateParameters(stmt, entity, metadata);
                
                logger.debug("Executing SQL: {}", sql);
                stmt.executeUpdate();
                
                return entity;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update entity", e);
        }
    }
    
    @Override
    public <T> T saveOrUpdate(T entity) {
        EntityMetadata metadata = getEntityMetadata(entity.getClass());
        ColumnMetadata pkColumn = metadata.getPrimaryKey();
        
        if (pkColumn != null) {
            Object id = pkColumn.getValue(entity);
            if (id != null && findById(entity.getClass(), id) != null) {
                return update(entity);
            }
        }
        
        return save(entity);
    }
    
    @Override
    public <T> void delete(T entity) {
        try {
            EntityMetadata metadata = getEntityMetadata(entity.getClass());
            ColumnMetadata pkColumn = metadata.getPrimaryKey();
            
            if (pkColumn == null) {
                throw new RuntimeException("Cannot delete entity without primary key");
            }
            
            String sql = buildDeleteSql(metadata);
            
            try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                Object id = pkColumn.getValue(entity);
                stmt.setObject(1, id);
                
                logger.debug("Executing SQL: {}", sql);
                stmt.executeUpdate();
                
                // Remove from cache
                entityCache.remove(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete entity", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T findById(Class<T> entityClass, Object id) {
        // Check cache first
        Object cached = entityCache.get(id);
        if (cached != null && entityClass.isInstance(cached)) {
            return (T) cached;
        }
        
        try {
            EntityMetadata metadata = getEntityMetadata(entityClass);
            String sql = buildSelectByIdSql(metadata);
            
            try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                stmt.setObject(1, id);
                
                logger.debug("Executing SQL: {}", sql);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        T entity = mapResultSetToEntity(rs, entityClass, metadata);
                        entityCache.put(id, entity);
                        return entity;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find entity by id", e);
        }
        
        return null;
    }
    
    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        try {
            EntityMetadata metadata = getEntityMetadata(entityClass);
            String sql = buildSelectAllSql(metadata);
            
            try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                logger.debug("Executing SQL: {}", sql);
                try (ResultSet rs = stmt.executeQuery()) {
                    List<T> results = new ArrayList<>();
                    while (rs.next()) {
                        T entity = mapResultSetToEntity(rs, entityClass, metadata);
                        results.add(entity);
                        
                        // Cache entity if it has a primary key
                        ColumnMetadata pkColumn = metadata.getPrimaryKey();
                        if (pkColumn != null) {
                            Object id = pkColumn.getValue(entity);
                            if (id != null) {
                                entityCache.put(id, entity);
                            }
                        }
                    }
                    return results;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all entities", e);
        }
    }
    
    @Override
    public <T> List<T> createQuery(String sql, Class<T> resultClass) {
        try {
            EntityMetadata metadata = getEntityMetadata(resultClass);
            
            try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                logger.debug("Executing SQL: {}", sql);
                try (ResultSet rs = stmt.executeQuery()) {
                    List<T> results = new ArrayList<>();
                    while (rs.next()) {
                        T entity = mapResultSetToEntity(rs, resultClass, metadata);
                        results.add(entity);
                    }
                    return results;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query", e);
        }
    }
    
    @Override
    public int executeUpdate(String sql) {
        try {
            try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                logger.debug("Executing SQL: {}", sql);
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute update", e);
        }
    }
    
    @Override
    public Transaction beginTransaction() {
        if (currentTransaction != null && currentTransaction.isActive()) {
            throw new RuntimeException("Transaction already active");
        }
        
        try {
            Connection conn = getConnection();
            conn.setAutoCommit(false);
            currentTransaction = new TransactionImpl(conn);
            return currentTransaction;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to begin transaction", e);
        }
    }
    
    @Override
    public Transaction getTransaction() {
        return currentTransaction;
    }
    
    @Override
    public void flush() {
        // In a full implementation, this would flush pending changes
        // For now, we're executing operations immediately
    }
    
    @Override
    public void clear() {
        entityCache.clear();
    }
    
    @Override
    public boolean isOpen() {
        return open;
    }
    
    @Override
    public void close() {
        if (open) {
            open = false;
            clear();
            
            if (currentTransaction != null && currentTransaction.isActive()) {
                currentTransaction.rollback();
            }
            
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.warn("Error closing connection", e);
                }
            }
        }
    }
    
    // Helper methods
    
    private EntityMetadata getEntityMetadata(Class<?> entityClass) {
        EntityMetadata metadata = entityMetadataMap.get(entityClass);
        if (metadata == null) {
            throw new RuntimeException("Entity class not registered: " + entityClass.getName());
        }
        return metadata;
    }
    
    private String buildInsertSql(EntityMetadata metadata) {
        StringBuilder sql = new StringBuilder();
        StringBuilder values = new StringBuilder();
        
        sql.append("INSERT INTO ").append(metadata.getFullTableName()).append(" (");
        values.append("VALUES (");
        
        boolean first = true;
        // Add basic columns
        for (ColumnMetadata column : metadata.getColumns().values()) {
            if (column.isPrimaryKey() && column.getGenerationType() != null) {
                continue; // Skip auto-generated primary keys
            }
            
            if (!first) {
                sql.append(", ");
                values.append(", ");
            }
            
            sql.append(column.getColumnName());
            values.append("?");
            first = false;
        }
        
        // Add foreign key columns for ManyToOne relationships
        for (RelationshipMetadata relationship : metadata.getRelationships().values()) {
            if (relationship.getRelationshipType() == RelationshipType.MANY_TO_ONE) {
                String joinColumn = relationship.getJoinColumn();
                if (joinColumn != null && !joinColumn.isEmpty()) {
                    if (!first) {
                        sql.append(", ");
                        values.append(", ");
                    }
                    
                    sql.append(joinColumn);
                    values.append("?");
                    first = false;
                }
            }
        }
        
        sql.append(") ").append(values).append(")");
        return sql.toString();
    }
    
    private String buildUpdateSql(EntityMetadata metadata) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(metadata.getFullTableName()).append(" SET ");
        
        boolean first = true;
        // Add basic columns
        for (ColumnMetadata column : metadata.getColumns().values()) {
            if (column.isPrimaryKey()) {
                continue; // Skip primary key in SET clause
            }
            
            if (!first) {
                sql.append(", ");
            }
            
            sql.append(column.getColumnName()).append(" = ?");
            first = false;
        }
        
        // Add foreign key columns for ManyToOne relationships
        for (RelationshipMetadata relationship : metadata.getRelationships().values()) {
            if (relationship.getRelationshipType() == RelationshipType.MANY_TO_ONE) {
                String joinColumn = relationship.getJoinColumn();
                if (joinColumn != null && !joinColumn.isEmpty()) {
                    if (!first) {
                        sql.append(", ");
                    }
                    
                    sql.append(joinColumn).append(" = ?");
                    first = false;
                }
            }
        }
        
        ColumnMetadata pkColumn = metadata.getPrimaryKey();
        if (pkColumn != null) {
            sql.append(" WHERE ").append(pkColumn.getColumnName()).append(" = ?");
        }
        
        return sql.toString();
    }
    
    private String buildDeleteSql(EntityMetadata metadata) {
        ColumnMetadata pkColumn = metadata.getPrimaryKey();
        return "DELETE FROM " + metadata.getFullTableName() + 
               " WHERE " + pkColumn.getColumnName() + " = ?";
    }
    
    private String buildSelectByIdSql(EntityMetadata metadata) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        
        boolean first = true;
        // Add basic columns
        for (ColumnMetadata column : metadata.getColumns().values()) {
            if (!first) {
                sql.append(", ");
            }
            sql.append(column.getColumnName());
            first = false;
        }
        
        // Add foreign key columns for ManyToOne relationships
        for (RelationshipMetadata relationship : metadata.getRelationships().values()) {
            if (relationship.getRelationshipType() == RelationshipType.MANY_TO_ONE) {
                String joinColumn = relationship.getJoinColumn();
                if (joinColumn != null && !joinColumn.isEmpty()) {
                    if (!first) {
                        sql.append(", ");
                    }
                    sql.append(joinColumn);
                    first = false;
                }
            }
        }
        
        sql.append(" FROM ").append(metadata.getFullTableName());
        
        ColumnMetadata pkColumn = metadata.getPrimaryKey();
        if (pkColumn != null) {
            sql.append(" WHERE ").append(pkColumn.getColumnName()).append(" = ?");
        }
        
        return sql.toString();
    }
    
    private String buildSelectAllSql(EntityMetadata metadata) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        
        boolean first = true;
        // Add basic columns
        for (ColumnMetadata column : metadata.getColumns().values()) {
            if (!first) {
                sql.append(", ");
            }
            sql.append(column.getColumnName());
            first = false;
        }
        
        // Add foreign key columns for ManyToOne relationships
        for (RelationshipMetadata relationship : metadata.getRelationships().values()) {
            if (relationship.getRelationshipType() == RelationshipType.MANY_TO_ONE) {
                String joinColumn = relationship.getJoinColumn();
                if (joinColumn != null && !joinColumn.isEmpty()) {
                    if (!first) {
                        sql.append(", ");
                    }
                    sql.append(joinColumn);
                    first = false;
                }
            }
        }
        
        sql.append(" FROM ").append(metadata.getFullTableName());
        return sql.toString();
    }
    
    private void setInsertParameters(PreparedStatement stmt, Object entity, EntityMetadata metadata) throws SQLException {
        int paramIndex = 1;
        // Set basic column values
        for (ColumnMetadata column : metadata.getColumns().values()) {
            if (column.isPrimaryKey() && column.getGenerationType() != null) {
                continue; // Skip auto-generated primary keys
            }
            
            Object value = column.getValue(entity);
            stmt.setObject(paramIndex++, value);
        }
        
        // Set foreign key values for ManyToOne relationships
        for (RelationshipMetadata relationship : metadata.getRelationships().values()) {
            if (relationship.getRelationshipType() == RelationshipType.MANY_TO_ONE) {
                String joinColumn = relationship.getJoinColumn();
                if (joinColumn != null && !joinColumn.isEmpty()) {
                    Object relatedEntity = relationship.getValue(entity);
                    Object foreignKeyValue = null;
                    
                    if (relatedEntity != null) {
                        // Get the primary key value from the related entity
                        EntityMetadata relatedMetadata = getEntityMetadata(relationship.getTargetEntity());
                        ColumnMetadata relatedPK = relatedMetadata.getPrimaryKey();
                        if (relatedPK != null) {
                            foreignKeyValue = relatedPK.getValue(relatedEntity);
                        }
                    }
                    
                    stmt.setObject(paramIndex++, foreignKeyValue);
                }
            }
        }
    }
    
    private void setUpdateParameters(PreparedStatement stmt, Object entity, EntityMetadata metadata) throws SQLException {
        int paramIndex = 1;
        
        // Set values for non-primary key columns
        for (ColumnMetadata column : metadata.getColumns().values()) {
            if (column.isPrimaryKey()) {
                continue;
            }
            
            Object value = column.getValue(entity);
            stmt.setObject(paramIndex++, value);
        }
        
        // Set foreign key values for ManyToOne relationships
        for (RelationshipMetadata relationship : metadata.getRelationships().values()) {
            if (relationship.getRelationshipType() == RelationshipType.MANY_TO_ONE) {
                String joinColumn = relationship.getJoinColumn();
                if (joinColumn != null && !joinColumn.isEmpty()) {
                    Object relatedEntity = relationship.getValue(entity);
                    Object foreignKeyValue = null;
                    
                    if (relatedEntity != null) {
                        // Get the primary key value from the related entity
                        EntityMetadata relatedMetadata = getEntityMetadata(relationship.getTargetEntity());
                        ColumnMetadata relatedPK = relatedMetadata.getPrimaryKey();
                        if (relatedPK != null) {
                            foreignKeyValue = relatedPK.getValue(relatedEntity);
                        }
                    }
                    
                    stmt.setObject(paramIndex++, foreignKeyValue);
                }
            }
        }
        
        // Set primary key value for WHERE clause
        ColumnMetadata pkColumn = metadata.getPrimaryKey();
        if (pkColumn != null) {
            Object pkValue = pkColumn.getValue(entity);
            stmt.setObject(paramIndex, pkValue);
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T> T mapResultSetToEntity(ResultSet rs, Class<T> entityClass, EntityMetadata metadata) throws SQLException {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            
            // Map basic columns
            for (ColumnMetadata column : metadata.getColumns().values()) {
                Object value = rs.getObject(column.getColumnName());
                if (value != null) {
                    column.setValue(entity, value);
                }
            }
            
            // Load ManyToOne relationships
            for (RelationshipMetadata relationship : metadata.getRelationships().values()) {
                if (relationship.getRelationshipType() == RelationshipType.MANY_TO_ONE) {
                    loadManyToOneRelationship(entity, relationship, rs);
                }
            }
            
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create entity instance", e);
        }
    }
    
    private void loadManyToOneRelationship(Object entity, RelationshipMetadata relationship, ResultSet rs) {
        try {
            // Get the foreign key column name (e.g., "user_id")
            String foreignKeyColumn = relationship.getJoinColumn();
            if (foreignKeyColumn.isEmpty()) {
                // Default naming: field name + "_id"
                foreignKeyColumn = relationship.getField().getName() + "_id";
            }
            
            // Get the foreign key value from the result set
            Object foreignKeyValue = rs.getObject(foreignKeyColumn);
            if (foreignKeyValue != null) {
                // Find the related entity by its primary key
                Class<?> targetEntityClass = relationship.getTargetEntity();
                Object relatedEntity = findById(targetEntityClass, foreignKeyValue);
                
                if (relatedEntity != null) {
                    // Set the relationship
                    relationship.setValue(entity, relatedEntity);
                }
            }
        } catch (SQLException e) {
            // Log the error but don't fail the entire mapping
            logger.warn("Failed to load ManyToOne relationship: {}", relationship.getField().getName(), e);
        }
    }
}
