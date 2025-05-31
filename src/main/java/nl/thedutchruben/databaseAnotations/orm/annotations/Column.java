package nl.thedutchruben.databaseAnotations.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the column mapping for a field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * The name of the column. If not specified, the field name will be used.
     */
    String name() default "";
    
    /**
     * Whether the column allows null values.
     */
    boolean nullable() default true;
    
    /**
     * Whether the column has a unique constraint.
     */
    boolean unique() default false;
    
    /**
     * The length of the column (for strings).
     */
    int length() default 255;
    
    /**
     * The precision for numeric columns.
     */
    int precision() default 0;
    
    /**
     * The scale for numeric columns.
     */
    int scale() default 0;
    
    /**
     * The column definition for DDL generation.
     */
    String columnDefinition() default "";
}
