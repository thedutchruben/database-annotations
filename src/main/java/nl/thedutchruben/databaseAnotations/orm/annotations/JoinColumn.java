package nl.thedutchruben.databaseAnotations.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a column for joining an entity association.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinColumn {
    /**
     * The name of the foreign key column.
     */
    String name() default "";
    
    /**
     * The name of the column referenced by this foreign key column.
     */
    String referencedColumnName() default "";
    
    /**
     * Whether the foreign key column is nullable.
     */
    boolean nullable() default true;
    
    /**
     * Whether the foreign key column is unique.
     */
    boolean unique() default false;
    
    /**
     * Whether the foreign key column is insertable.
     */
    boolean insertable() default true;
    
    /**
     * Whether the foreign key column is updatable.
     */
    boolean updatable() default true;
    
    /**
     * The foreign key constraint definition.
     */
    String foreignKey() default "";
}
