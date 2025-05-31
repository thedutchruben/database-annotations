package nl.thedutchruben.databaseAnotations.orm.dialect;

import nl.thedutchruben.databaseAnotations.orm.core.ColumnMetadata;

/**
 * SQLite database dialect.
 */
public class SQLiteDialect extends Dialect {
    
    @Override
    public String getLimitString(String sql, int limit) {
        return sql + " LIMIT " + limit;
    }
    
    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }
    
    @Override
    public String getIdentityColumnString() {
        return "AUTOINCREMENT";
    }
    
    @Override
    public boolean supportsSequences() {
        return false;
    }
    
    @Override
    public String getSequenceNextValString(String sequenceName) {
        throw new UnsupportedOperationException("SQLite does not support sequences");
    }
    
    @Override
    public String getIdentitySelectString() {
        return "SELECT last_insert_rowid()";
    }
    
    @Override
    public String getColumnType(ColumnMetadata columnMeta) {
        Class<?> javaType = columnMeta.getJavaType();
        
        if (!columnMeta.getColumnDefinition().isEmpty()) {
            return columnMeta.getColumnDefinition();
        }
        
        // SQLite has dynamic typing, but we'll use affinity types
        if (javaType == String.class) {
            return "TEXT";
        } else if (javaType == Integer.class || javaType == int.class ||
                   javaType == Long.class || javaType == long.class) {
            return "INTEGER";
        } else if (javaType == Double.class || javaType == double.class ||
                   javaType == Float.class || javaType == float.class) {
            return "REAL";
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            return "INTEGER"; // SQLite doesn't have boolean, use integer
        } else if (javaType == java.sql.Date.class ||
                   javaType == java.sql.Time.class ||
                   javaType == java.sql.Timestamp.class ||
                   javaType == java.util.Date.class) {
            return "TEXT"; // SQLite stores dates as text or integer
        } else {
            return "TEXT";
        }
    }
    
    @Override
    public String getDropTableString() {
        return "DROP TABLE IF EXISTS";
    }
}
