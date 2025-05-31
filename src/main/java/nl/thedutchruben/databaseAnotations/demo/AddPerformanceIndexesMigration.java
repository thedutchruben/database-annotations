package nl.thedutchruben.databaseAnotations.demo;

import nl.thedutchruben.databaseAnotations.orm.migration.Migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Migration to add performance indexes to the database.
 */
public class AddPerformanceIndexesMigration implements Migration {
    
    @Override
    public String getVersion() {
        return "002_add_performance_indexes";
    }
    
    @Override
    public String getDescription() {
        return "Add performance indexes for common queries";
    }
    
    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Add index on users.username for login queries
            stmt.execute("CREATE INDEX idx_users_username ON users(username)");
            
            // Add index on posts.user_id for user's posts queries
            stmt.execute("CREATE INDEX idx_posts_user_id ON posts(user_id)");
            
            // Add index on posts.created_at for chronological queries
            stmt.execute("CREATE INDEX idx_posts_created_at ON posts(created_at)");
            
            // Add index on comments.post_id for post's comments queries
            stmt.execute("CREATE INDEX idx_comments_post_id ON comments(post_id)");
            
            // Add composite index for comments by post and creation date
            stmt.execute("CREATE INDEX idx_comments_post_created ON comments(post_id, created_at)");
        }
    }
    
    @Override
    public void down(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP INDEX IF EXISTS idx_users_username");
            stmt.execute("DROP INDEX IF EXISTS idx_posts_user_id");
            stmt.execute("DROP INDEX IF EXISTS idx_posts_created_at");
            stmt.execute("DROP INDEX IF EXISTS idx_comments_post_id");
            stmt.execute("DROP INDEX IF EXISTS idx_comments_post_created");
        }
    }
}
