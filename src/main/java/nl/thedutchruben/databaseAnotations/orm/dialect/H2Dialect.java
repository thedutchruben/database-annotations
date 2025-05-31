package nl.thedutchruben.databaseAnotations.orm.dialect;

import nl.thedutchruben.databaseAnotations.orm.core.ColumnMetadata;

/**
 * H2 database dialect.
 */
public class H2Dialect extends Dialect {
    
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
        return "AUTO_INCREMENT";
    }
    
    @Override
    public boolean supportsSequences() {
        return true;
    }
    
    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "SELECT nextval('" + sequenceName + "')";
    }
    
    @Override
    public String getIdentitySelectString() {
        return "SELECT IDENTITY()";
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
            return "INTEGER";
        } else if (javaType == Long.class || javaType == long.class) {
            return "BIGINT";
        } else if (javaType == Double.class || javaType == double.class) {
            if (columnMeta.getPrecision() > 0) {
                return "DECIMAL(" + columnMeta.getPrecision() + "," + columnMeta.getScale() + ")";
            }
            return "DOUBLE";
        } else if (javaType == Float.class || javaType == float.class) {
            return "REAL";
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            return "BOOLEAN";
        } else if (javaType == java.sql.Date.class) {
            return "DATE";
        } else if (javaType == java.sql.Time.class) {
            return "TIME";
        } else if (javaType == java.sql.Timestamp.class || javaType == java.util.Date.class) {
            return "TIMESTAMP";
        } else {
            return "VARCHAR(255)";
        }
    }
    
    @Override
    public String getDropTableString() {
        return "DROP TABLE IF EXISTS";
    }
}
