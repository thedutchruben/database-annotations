package nl.thedutchruben.databaseAnotations.orm.session;

/**
 * Factory for creating database sessions.
 */
public interface SessionFactory extends AutoCloseable {
    
    /**
     * Opens a new session.
     */
    Session openSession();
    
    /**
     * Gets the current session bound to this thread.
     */
    Session getCurrentSession();
    
    /**
     * Checks if the session factory is closed.
     */
    boolean isClosed();
    
    /**
     * Closes the session factory and releases all resources.
     */
    @Override
    void close();
}
