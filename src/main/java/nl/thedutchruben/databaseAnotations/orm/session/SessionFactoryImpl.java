package nl.thedutchruben.databaseAnotations.orm.session;

import com.zaxxer.hikari.HikariDataSource;
import nl.thedutchruben.databaseAnotations.orm.core.Configuration;
import nl.thedutchruben.databaseAnotations.orm.core.EntityMetadata;
import nl.thedutchruben.databaseAnotations.orm.dialect.Dialect;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of SessionFactory.
 */
public class SessionFactoryImpl implements SessionFactory {
    
    private final DataSource dataSource;
    private final Dialect dialect;
    private final Map<Class<?>, EntityMetadata> entityMetadataMap;
    private final ThreadLocal<Session> currentSession = new ThreadLocal<>();
    private volatile boolean closed = false;
    
    public SessionFactoryImpl(Configuration configuration) {
        this.dataSource = configuration.getDataSource();
        this.dialect = configuration.getDialect();
        this.entityMetadataMap = new ConcurrentHashMap<>();
        
        // Register all entity classes
        for (Class<?> entityClass : configuration.getEntityClasses()) {
            EntityMetadata metadata = new EntityMetadata(entityClass);
            entityMetadataMap.put(entityClass, metadata);
        }
    }
    
    @Override
    public Session openSession() {
        checkClosed();
        return new SessionImpl(dataSource, dialect, entityMetadataMap);
    }
    
    @Override
    public Session getCurrentSession() {
        checkClosed();
        Session session = currentSession.get();
        if (session == null || !session.isOpen()) {
            session = openSession();
            currentSession.set(session);
        }
        return session;
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }
    
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            
            // Close current sessions
            Session session = currentSession.get();
            if (session != null && session.isOpen()) {
                session.close();
            }
            currentSession.remove();
            
            // Close data source if it's HikariCP
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).close();
            }
        }
    }
    
    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("SessionFactory is closed");
        }
    }
    
    public Map<Class<?>, EntityMetadata> getEntityMetadataMap() {
        return entityMetadataMap;
    }
}
