package nl.thedutchruben.databaseAnotations.orm.exception;

/**
 * Thrown when there are query execution errors.
 */
public class QueryException extends ORMException {
    
    private final String sql;
    
    public QueryException(String message, String sql) {
        super(message + " [SQL: " + sql + "]");
        this.sql = sql;
    }
    
    public QueryException(String message, String sql, Throwable cause) {
        super(message + " [SQL: " + sql + "]", cause);
        this.sql = sql;
    }
    
    public String getSql() {
        return sql;
    }
    
    public static QueryException executionFailed(String sql, Throwable cause) {
        return new QueryException("Query execution failed", sql, cause);
    }
    
    public static QueryException noResult(String sql) {
        return new QueryException("Query returned no results", sql);
    }
    
    public static QueryException tooManyResults(String sql) {
        return new QueryException("Query returned more than one result", sql);
    }
    
    public static QueryException invalidParameter(String sql, String paramName) {
        return new QueryException("Invalid parameter: " + paramName, sql);
    }
}
