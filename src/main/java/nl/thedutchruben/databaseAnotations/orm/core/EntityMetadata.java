package nl.thedutchruben.databaseAnotations.orm.core;

import nl.thedutchruben.databaseAnotations.orm.annotations.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Holds metadata information about an entity class.
 */
public class EntityMetadata {
    private Class<?> entityClass;
    private String tableName;
    private String schema;
    private String catalog;
    private Map<String, ColumnMetadata> columns;
    private ColumnMetadata primaryKey;
    private Map<String, RelationshipMetadata> relationships;
    
    public EntityMetadata(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.columns = new HashMap<>();
        this.relationships = new HashMap<>();
        parseMetadata();
    }
    
    private void parseMetadata() {
        // Parse table information
        parseTableInfo();
        
        // Parse fields
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(Id.class)) {
                parsePrimaryKey(field);
            } else if (field.isAnnotationPresent(Column.class) || isBasicType(field.getType())) {
                parseColumn(field);
            } else if (isRelationshipField(field)) {
                parseRelationship(field);
            }
        }
    }
    
    private void parseTableInfo() {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            this.tableName = table.name();
            this.schema = table.schema().isEmpty() ? null : table.schema();
            this.catalog = table.catalog().isEmpty() ? null : table.catalog();
        } else if (entityClass.isAnnotationPresent(Entity.class)) {
            Entity entity = entityClass.getAnnotation(Entity.class);
            this.tableName = entity.name().isEmpty() ? entityClass.getSimpleName() : entity.name();
        } else {
            this.tableName = entityClass.getSimpleName();
        }
    }
    
    private void parsePrimaryKey(Field field) {
        ColumnMetadata columnMeta = new ColumnMetadata(field);
        columnMeta.setPrimaryKey(true);
        
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            GeneratedValue genValue = field.getAnnotation(GeneratedValue.class);
            columnMeta.setGenerationType(genValue.strategy());
            columnMeta.setGenerator(genValue.generator());
        }
        
        this.primaryKey = columnMeta;
        this.columns.put(field.getName(), columnMeta);
    }
    
    private void parseColumn(Field field) {
        ColumnMetadata columnMeta = new ColumnMetadata(field);
        this.columns.put(field.getName(), columnMeta);
    }
    
    private void parseRelationship(Field field) {
        RelationshipMetadata relationMeta = new RelationshipMetadata(field);
        this.relationships.put(field.getName(), relationMeta);
    }
    
    private boolean isBasicType(Class<?> type) {
        return type.isPrimitive() || 
               type == String.class ||
               type == Integer.class ||
               type == Long.class ||
               type == Double.class ||
               type == Float.class ||
               type == Boolean.class ||
               type == java.sql.Date.class ||
               type == java.sql.Time.class ||
               type == java.sql.Timestamp.class ||
               type == java.util.Date.class ||
               Number.class.isAssignableFrom(type);
    }
    
    private boolean isRelationshipField(Field field) {
        return field.isAnnotationPresent(OneToMany.class) ||
               field.isAnnotationPresent(ManyToOne.class) ||
               field.isAnnotationPresent(OneToOne.class);
    }
    
    // Getters
    public Class<?> getEntityClass() { return entityClass; }
    public String getTableName() { return tableName; }
    public String getSchema() { return schema; }
    public String getCatalog() { return catalog; }
    public Map<String, ColumnMetadata> getColumns() { return columns; }
    public ColumnMetadata getPrimaryKey() { return primaryKey; }
    public Map<String, RelationshipMetadata> getRelationships() { return relationships; }
    
    public String getFullTableName() {
        StringBuilder sb = new StringBuilder();
        if (catalog != null) {
            sb.append(catalog).append(".");
        }
        if (schema != null) {
            sb.append(schema).append(".");
        }
        sb.append(tableName);
        return sb.toString();
    }
}
