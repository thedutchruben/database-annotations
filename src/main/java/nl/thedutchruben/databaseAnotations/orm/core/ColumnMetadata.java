package nl.thedutchruben.databaseAnotations.orm.core;

import nl.thedutchruben.databaseAnotations.orm.annotations.Column;
import nl.thedutchruben.databaseAnotations.orm.annotations.GenerationType;
import java.lang.reflect.Field;

/**
 * Holds metadata information about a column.
 */
public class ColumnMetadata {
    private Field field;
    private String columnName;
    private boolean nullable = true;
    private boolean unique = false;
    private int length = 255;
    private int precision = 0;
    private int scale = 0;
    private String columnDefinition = "";
    private boolean primaryKey = false;
    private GenerationType generationType;
    private String generator = "";
    
    public ColumnMetadata(Field field) {
        this.field = field;
        this.columnName = field.getName();
        parseColumnAnnotation();
    }
    
    private void parseColumnAnnotation() {
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            if (!column.name().isEmpty()) {
                this.columnName = column.name();
            }
            this.nullable = column.nullable();
            this.unique = column.unique();
            this.length = column.length();
            this.precision = column.precision();
            this.scale = column.scale();
            this.columnDefinition = column.columnDefinition();
        }
    }
    
    public Object getValue(Object entity) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get value from field: " + field.getName(), e);
        }
    }
    
    public void setValue(Object entity, Object value) {
        try {
            field.setAccessible(true);
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set value to field: " + field.getName(), e);
        }
    }
    
    // Getters and setters
    public Field getField() { return field; }
    public String getColumnName() { return columnName; }
    public boolean isNullable() { return nullable; }
    public boolean isUnique() { return unique; }
    public int getLength() { return length; }
    public int getPrecision() { return precision; }
    public int getScale() { return scale; }
    public String getColumnDefinition() { return columnDefinition; }
    public boolean isPrimaryKey() { return primaryKey; }
    public GenerationType getGenerationType() { return generationType; }
    public String getGenerator() { return generator; }
    
    public void setPrimaryKey(boolean primaryKey) { this.primaryKey = primaryKey; }
    public void setGenerationType(GenerationType generationType) { this.generationType = generationType; }
    public void setGenerator(String generator) { this.generator = generator; }
    
    public Class<?> getJavaType() {
        return field.getType();
    }
}
