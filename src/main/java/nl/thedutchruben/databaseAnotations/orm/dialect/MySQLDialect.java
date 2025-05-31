package nl.thedutchruben.databaseAnotations.orm.dialect;

import nl.thedutchruben.databaseAnotations.orm.core.ColumnMetadata;

/**
 * MySQL database dialect.
 */
public class MySQLDialect extends Dialect {
    
    @Override
    public String getLimitString(String sql, int limit) {
        return sql + " LIMIT " + limit;
    }
    
    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return sql + " LIMIT " + offset + ", " + limit;
    }
    
    @Override
    public String getIdentityColumnString() {
        return "AUTO_INCREMENT";
    }
    
    @Override
    public boolean supportsSequences() {
        return false;
    }
    
    @Override
    public String getSequenceNextValString(String sequenceName) {
        throw new UnsupportedOperationException("MySQL does not support sequences");
    }
    
    @Override
    public String getIdentitySelectString() {
        return "SELECT LAST_INSERT_ID()";
    }
    
    @Override
    public String getColumnType(ColumnMetadata columnMeta) {
        Class<?> javaType = columnMeta.getJavaType();
        
        if (!columnMeta.getColumnDefinition().isEmpty()) {
            return columnMeta.getColumnDefinition();
        }
        
        if (javaType == String.class) {
            return "VARCHAR(" + columnMeta.getLength() + ")";
        } else if (javaType == Integer.class || javaType == int.class) {
            return "INT";
        } else if (javaType == Long.class || javaType == long.class) {
            return "BIGINT";
        } else if (javaType == Double.class || javaType == double.class) {
            if (columnMeta.getPrecision() > 0) {
                return "DECIMAL(" + columnMeta.getPrecision() + "," + columnMeta.getScale() + ")";
            }
            return "DOUBLE";
        } else if (javaType == Float.class || javaType == float.class) {
            return "FLOAT";
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            return "BOOLEAN";
        } else if (javaType == java.sql.Date.class) {
            return "DATE";
        } else if (javaType == java.sql.Time.class) {
            return "TIME";
        } else if (javaType == java.sql.Timestamp.class || javaType == java.util.Date.class) {
            return "TIMESTAMP";
        } else {
            return "TEXT";
        }
    }
    
    @Override
    public String quote(String identifier) {
        return "`" + identifier + "`";
    }
}
