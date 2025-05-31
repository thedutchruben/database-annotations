package nl.thedutchruben.databaseAnotations.orm.query;

import nl.thedutchruben.databaseAnotations.orm.core.EntityMetadata;
import nl.thedutchruben.databaseAnotations.orm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of Query interface for executing SQL queries.
 */
public class QueryImpl<T> implements Query<T> {
    
    private static final Logger logger = LoggerFactory.getLogger(QueryImpl.class);
    
    private final Session session;
    private final String sql;
    private final Class<T> resultClass;
    private final EntityMetadata metadata;
    private final Map<String, Object> parameters = new HashMap<>();
    
    private Integer maxResults;
    private Integer firstResult;
    
    public QueryImpl(Session session, String sql, Class<T> resultClass, EntityMetadata metadata) {
        this.session = session;
        this.sql = sql;
        this.resultClass = resultClass;
        this.metadata = metadata;
    }
    
    @Override
    public List<T> getResultList() {
        try {
            String finalSql = applyLimits(sql);
            logger.debug("Executing query: {}", finalSql);
            
            return session.createQuery(finalSql, resultClass);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute query", e);
        }
    }
    
    @Override
    public T getSingleResult() {
        List<T> results = getResultList();
        
        if (results.isEmpty()) {
            throw new RuntimeException("No result found");
        }
        
        if (results.size() > 1) {
            throw new RuntimeException("More than one result found");
        }
        
        return results.get(0);
    }
    
    @Override
    public Optional<T> getSingleResultOptional() {
        List<T> results = getResultList();
        
        if (results.isEmpty()) {
            return Optional.empty();
        }
        
        if (results.size() > 1) {
            throw new RuntimeException("More than one result found");
        }
        
        return Optional.of(results.get(0));
    }
    
    @Override
    public Query<T> setParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }
    
    @Override
    public Query<T> setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }
    
    @Override
    public Query<T> setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }
    
    @Override
    public String getQueryString() {
        return sql;
    }
    
    private String applyLimits(String sql) {
        String finalSql = sql;
        
        // Replace named parameters
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            String placeholder = ":" + param.getKey();
            String value = formatParameterValue(param.getValue());
            finalSql = finalSql.replace(placeholder, value);
        }
        
        // Apply LIMIT and OFFSET if specified
        if (maxResults != null) {
            if (firstResult != null) {
                finalSql += " LIMIT " + firstResult + ", " + maxResults;
            } else {
                finalSql += " LIMIT " + maxResults;
            }
        }
        
        return finalSql;
    }
    
    private String formatParameterValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof String) {
            return "'" + value.toString().replace("'", "''") + "'";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else {
            return "'" + value.toString() + "'";
        }
    }
}
