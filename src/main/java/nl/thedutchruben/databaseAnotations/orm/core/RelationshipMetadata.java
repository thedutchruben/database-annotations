package nl.thedutchruben.databaseAnotations.orm.core;

import nl.thedutchruben.databaseAnotations.orm.annotations.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Holds metadata information about a relationship.
 */
public class RelationshipMetadata {
    private Field field;
    private RelationshipType relationshipType;
    private Class<?> targetEntity;
    private String mappedBy = "";
    private CascadeType[] cascade = {};
    private FetchType fetch = FetchType.LAZY;
    private boolean optional = true;
    private String joinColumn = "";
    private String referencedColumn = "";
    
    public RelationshipMetadata(Field field) {
        this.field = field;
        parseRelationship();
    }
    
    private void parseRelationship() {
        if (field.isAnnotationPresent(OneToMany.class)) {
            parseOneToMany();
        } else if (field.isAnnotationPresent(ManyToOne.class)) {
            parseManyToOne();
        } else if (field.isAnnotationPresent(OneToOne.class)) {
            parseOneToOne();
        }
        
        // Always try to parse join column, or set default for relationships that need it
        if (field.isAnnotationPresent(JoinColumn.class)) {
            parseJoinColumn();
        } else if (relationshipType == RelationshipType.MANY_TO_ONE || relationshipType == RelationshipType.ONE_TO_ONE) {
            // Set default join column name
            this.joinColumn = field.getName() + "_id";
            this.referencedColumn = "id";
        }
    }
    
    private void parseOneToMany() {
        this.relationshipType = RelationshipType.ONE_TO_MANY;
        OneToMany annotation = field.getAnnotation(OneToMany.class);
        
        this.mappedBy = annotation.mappedBy();
        this.cascade = annotation.cascade();
        this.fetch = annotation.fetch();
        this.optional = annotation.optional();
        
        if (annotation.targetEntity() != void.class) {
            this.targetEntity = annotation.targetEntity();
        } else {
            // Infer from generic type
            this.targetEntity = getGenericType();
        }
    }
    
    private void parseManyToOne() {
        this.relationshipType = RelationshipType.MANY_TO_ONE;
        ManyToOne annotation = field.getAnnotation(ManyToOne.class);
        
        this.cascade = annotation.cascade();
        this.fetch = annotation.fetch();
        this.optional = annotation.optional();
        
        if (annotation.targetEntity() != void.class) {
            this.targetEntity = annotation.targetEntity();
        } else {
            this.targetEntity = field.getType();
        }
    }
    
    private void parseOneToOne() {
        this.relationshipType = RelationshipType.ONE_TO_ONE;
        OneToOne annotation = field.getAnnotation(OneToOne.class);
        
        this.mappedBy = annotation.mappedBy();
        this.cascade = annotation.cascade();
        this.fetch = annotation.fetch();
        this.optional = annotation.optional();
        
        if (annotation.targetEntity() != void.class) {
            this.targetEntity = annotation.targetEntity();
        } else {
            this.targetEntity = field.getType();
        }
    }
    
    private void parseJoinColumn() {
        JoinColumn annotation = field.getAnnotation(JoinColumn.class);
        this.joinColumn = annotation.name().isEmpty() ? 
            field.getName() + "_id" : annotation.name();
        this.referencedColumn = annotation.referencedColumnName().isEmpty() ? 
            "id" : annotation.referencedColumnName();
    }
    
    private Class<?> getGenericType() {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypes = paramType.getActualTypeArguments();
            if (actualTypes.length > 0) {
                return (Class<?>) actualTypes[0];
            }
        }
        return Object.class;
    }
    
    public boolean isCollection() {
        return Collection.class.isAssignableFrom(field.getType());
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
    
    // Getters
    public Field getField() { return field; }
    public RelationshipType getRelationshipType() { return relationshipType; }
    public Class<?> getTargetEntity() { return targetEntity; }
    public String getMappedBy() { return mappedBy; }
    public CascadeType[] getCascade() { return cascade; }
    public FetchType getFetch() { return fetch; }
    public boolean isOptional() { return optional; }
    public String getJoinColumn() { return joinColumn; }
    public String getReferencedColumn() { return referencedColumn; }
}
