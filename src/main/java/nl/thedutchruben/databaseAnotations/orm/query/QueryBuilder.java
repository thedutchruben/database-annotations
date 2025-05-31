package nl.thedutchruben.databaseAnotations.orm.query;

import nl.thedutchruben.databaseAnotations.orm.core.EntityMetadata;
import nl.thedutchruben.databaseAnotations.orm.dialect.Dialect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluent query builder for constructing SQL queries.
 */
public class QueryBuilder<T> {
    
    private final Class<T> entityClass;
    private final EntityMetadata metadata;
    private final Dialect dialect;
    
    private final List<String> selectColumns = new ArrayList<>();
    private final List<String> whereConditions = new ArrayList<>();
    private final List<String> orderByColumns = new ArrayList<>();
    private final Map<String, Object> parameters = new HashMap<>();
    
    private String alias;
    private Integer limitValue;
    private Integer offsetValue;
    private boolean distinct = false;
    
    public QueryBuilder(Class<T> entityClass, EntityMetadata metadata, Dialect dialect) {
        this.entityClass = entityClass;
        this.metadata = metadata;
        this.dialect = dialect;
    }
    
    /**
     * Sets the alias for the main table.
     */
    public QueryBuilder<T> alias(String alias) {
        this.alias = alias;
        return this;
    }
    
    /**
     * Adds SELECT DISTINCT to the query.
     */
    public QueryBuilder<T> distinct() {
        this.distinct = true;
        return this;
    }
    
    /**
     * Adds specific columns to select (default is all columns).
     */
    public QueryBuilder<T> select(String... columns) {
        for (String column : columns) {
            selectColumns.add(column);
        }
        return this;
    }
    
    /**
     * Adds a WHERE condition with parameters.
     */
    public QueryBuilder<T> where(String condition) {
        whereConditions.add(condition);
        return this;
    }
    
    /**
     * Adds a WHERE condition with a parameter.
     */
    public QueryBuilder<T> where(String condition, Object value) {
        whereConditions.add(condition);
        String paramName = "param" + parameters.size();
        parameters.put(paramName, value);
        return this;
    }
    
    /**
     * Adds an AND WHERE condition.
     */
    public QueryBuilder<T> and(String condition) {
        if (!whereConditions.isEmpty()) {
            whereConditions.add("AND " + condition);
        } else {
            whereConditions.add(condition);
        }
        return this;
    }
    
    /**
     * Adds an OR WHERE condition.
     */
    public QueryBuilder<T> or(String condition) {
        if (!whereConditions.isEmpty()) {
            whereConditions.add("OR " + condition);
        } else {
            whereConditions.add(condition);
        }
        return this;
    }
    
    /**
     * Adds an ORDER BY clause.
     */
    public QueryBuilder<T> orderBy(String column) {
        orderByColumns.add(column + " ASC");
        return this;
    }
    
    /**
     * Adds an ORDER BY DESC clause.
     */
    public QueryBuilder<T> orderByDesc(String column) {
        orderByColumns.add(column + " DESC");
        return this;
    }
    
    /**
     * Sets the LIMIT for the query.
     */
    public QueryBuilder<T> limit(int limit) {
        this.limitValue = limit;
        return this;
    }
    
    /**
     * Sets the OFFSET for the query.
     */
    public QueryBuilder<T> offset(int offset) {
        this.offsetValue = offset;
        return this;
    }
    
    /**
     * Sets a named parameter value.
     */
    public QueryBuilder<T> setParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }
    
    /**
     * Builds the final SQL query string.
     */
    public String buildQuery() {
        StringBuilder sql = new StringBuilder();
        
        // SELECT clause
        sql.append("SELECT ");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        
        if (selectColumns.isEmpty()) {
            // Select all columns
            boolean first = true;
            for (String columnName : metadata.getColumns().keySet()) {
                if (!first) sql.append(", ");
                if (alias != null) {
                    sql.append(alias).append(".");
                }
                sql.append(columnName);
                first = false;
            }
        } else {
            sql.append(String.join(", ", selectColumns));
        }
        
        // FROM clause
        sql.append(" FROM ").append(metadata.getFullTableName());
        if (alias != null) {
            sql.append(" ").append(alias);
        }
        
        // WHERE clause
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" ", whereConditions));
        }
        
        // ORDER BY clause
        if (!orderByColumns.isEmpty()) {
            sql.append(" ORDER BY ").append(String.join(", ", orderByColumns));
        }
        
        // LIMIT and OFFSET
        String finalSql = sql.toString();
        if (limitValue != null) {
            if (offsetValue != null) {
                finalSql = dialect.getLimitString(finalSql, offsetValue, limitValue);
            } else {
                finalSql = dialect.getLimitString(finalSql, limitValue);
            }
        }
        
        return finalSql;
    }
    
    /**
     * Gets the parameter values for prepared statement binding.
     */
    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }
    
    /**
     * Gets the entity class this query is for.
     */
    public Class<T> getEntityClass() {
        return entityClass;
    }
}
