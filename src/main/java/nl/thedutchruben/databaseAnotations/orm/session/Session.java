package nl.thedutchruben.databaseAnotations.orm.session;

import java.util.List;

/**
 * Interface for database session operations.
 * Provides methods for CRUD operations and transaction management.
 */
public interface Session extends AutoCloseable {
    
    /**
     * Saves an entity to the database.
     */
    <T> T save(T entity);
    
    /**
     * Updates an existing entity in the database.
     */
    <T> T update(T entity);
    
    /**
     * Saves or updates an entity in the database.
     */
    <T> T saveOrUpdate(T entity);
    
    /**
     * Deletes an entity from the database.
     */
    <T> void delete(T entity);
    
    /**
     * Finds an entity by its primary key.
     */
    <T> T findById(Class<T> entityClass, Object id);
    
    /**
     * Finds all entities of a given type.
     */
    <T> List<T> findAll(Class<T> entityClass);
    
    /**
     * Executes a custom SQL query and returns the result list.
     */
    <T> List<T> createQuery(String sql, Class<T> resultClass);
    
    /**
     * Executes a native SQL query.
     */
    int executeUpdate(String sql);
    
    /**
     * Begins a new transaction.
     */
    Transaction beginTransaction();
    
    /**
     * Gets the current transaction.
     */
    Transaction getTransaction();
    
    /**
     * Flushes pending changes to the database.
     */
    void flush();
    
    /**
     * Clears the session cache.
     */
    void clear();
    
    /**
     * Checks if the session is open.
     */
    boolean isOpen();
    
    /**
     * Closes the session.
     */
    @Override
    void close();
}
