package nl.thedutchruben.databaseAnotations.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the generation strategy for primary keys.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GeneratedValue {
    /**
     * The strategy to use for generating values.
     */
    GenerationType strategy() default GenerationType.AUTO;
    
    /**
     * The name of the generator to use (for SEQUENCE and TABLE strategies).
     */
    String generator() default "";
}
