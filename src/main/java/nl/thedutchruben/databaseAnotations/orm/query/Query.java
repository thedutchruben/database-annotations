package nl.thedutchruben.databaseAnotations.orm.query;

import java.util.List;
import java.util.Optional;

/**
 * Interface for executing queries built by QueryBuilder.
 */
public interface Query<T> {
    
    /**
     * Executes the query and returns a list of results.
     */
    List<T> getResultList();
    
    /**
     * Executes the query and returns a single result.
     * @throws RuntimeException if more than one result is found
     */
    T getSingleResult();
    
    /**
     * Executes the query and returns an optional single result.
     */
    Optional<T> getSingleResultOptional();
    
    /**
     * Sets a parameter value.
     */
    Query<T> setParameter(String name, Object value);
    
    /**
     * Sets the maximum number of results to retrieve.
     */
    Query<T> setMaxResults(int maxResults);
    
    /**
     * Sets the position of the first result to retrieve.
     */
    Query<T> setFirstResult(int firstResult);
    
    /**
     * Gets the underlying SQL query string.
     */
    String getQueryString();
}
