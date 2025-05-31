package nl.thedutchruben.databaseAnotations.orm.dialect;

import nl.thedutchruben.databaseAnotations.orm.core.ColumnMetadata;

/**
 * Base class for all database dialects.
 * Provides database-specific SQL generation and feature support.
 */
public abstract class Dialect {
    
    /**
     * Gets the SQL for limiting query results.
     */
    public abstract String getLimitString(String sql, int limit);
    
    /**
     * Gets the SQL for limiting query results with offset.
     */
    public abstract String getLimitString(String sql, int offset, int limit);
    
    /**
     * Gets the identity column definition for auto-generated primary keys.
     */
    public abstract String getIdentityColumnString();
    
    /**
     * Checks if this dialect supports sequences.
     */
    public abstract boolean supportsSequences();
    
    /**
     * Gets the SQL to retrieve the next value from a sequence.
     */
    public abstract String getSequenceNextValString(String sequenceName);
    
    /**
     * Gets the SQL to retrieve the last generated identity value.
     */
    public abstract String getIdentitySelectString();
    
    /**
     * Maps Java types to database-specific column types.
     */
    public abstract String getColumnType(ColumnMetadata columnMeta);
    
    /**
     * Gets the SQL keyword for dropping a table if it exists.
     */
    public String getDropTableString() {
        return "DROP TABLE IF EXISTS";
    }
    
    /**
     * Gets the SQL for creating a table.
     */
    public String getCreateTableString() {
        return "CREATE TABLE";
    }
    
    /**
     * Gets the SQL for adding a primary key constraint.
     */
    public String getPrimaryKeyString() {
        return "PRIMARY KEY";
    }
    
    /**
     * Gets the SQL for adding a foreign key constraint.
     */
    public String getForeignKeyString() {
        return "FOREIGN KEY";
    }
    
    /**
     * Gets the SQL for adding a unique constraint.
     */
    public String getUniqueString() {
        return "UNIQUE";
    }
    
    /**
     * Gets the SQL for adding a not null constraint.
     */
    public String getNotNullString() {
        return "NOT NULL";
    }
    
    /**
     * Quotes an identifier if necessary.
     */
    public String quote(String identifier) {
        return "\"" + identifier + "\"";
    }
    
    /**
     * Escapes a string literal.
     */
    public String escape(String literal) {
        return literal.replace("'", "''");
    }
}
