package nl.thedutchruben.databaseAnotations.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the table for an entity.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    /**
     * The name of the table.
     */
    String name();
    
    /**
     * The catalog of the table.
     */
    String catalog() default "";
    
    /**
     * The schema of the table.
     */
    String schema() default "";
}
