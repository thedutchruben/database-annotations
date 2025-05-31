package nl.thedutchruben.databaseAnotations.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a many-to-one relationship between two entities.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {
    /**
     * The entity class that is the target of the association.
     */
    Class<?> targetEntity() default void.class;
    
    /**
     * The operations that must be cascaded to the target of the association.
     */
    CascadeType[] cascade() default {};
    
    /**
     * Whether the association should be lazily loaded or must be eagerly fetched.
     */
    FetchType fetch() default FetchType.EAGER;
    
    /**
     * Whether the association is optional.
     */
    boolean optional() default true;
}
