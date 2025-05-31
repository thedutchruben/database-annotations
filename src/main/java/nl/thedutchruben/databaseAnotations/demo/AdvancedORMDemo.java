package nl.thedutchruben.databaseAnotations.demo;

import nl.thedutchruben.databaseAnotations.orm.core.Configuration;
import nl.thedutchruben.databaseAnotations.orm.core.SchemaGenerator;
import nl.thedutchruben.databaseAnotations.orm.session.Session;
import nl.thedutchruben.databaseAnotations.orm.session.SessionFactory;
import nl.thedutchruben.databaseAnotations.orm.session.SessionFactoryImpl;
import nl.thedutchruben.databaseAnotations.orm.session.Transaction;
import nl.thedutchruben.databaseAnotations.orm.util.PerformanceMonitor;

import java.util.List;

/**
 * Advanced demo showing more ORM features including query builder and performance monitoring.
 */
public class AdvancedORMDemo {
    
    public static void main(String[] args) {
        // Configure the ORM with performance monitoring
        Configuration config = new Configuration()
                .database("jdbc:h2:mem:advanceddb", "sa", "")
                .addEntity(User.class)
                .addEntity(Post.class)
                .addEntity(Comment.class)
                .setProperty("orm.show_sql", "true")
                .setProperty("orm.format_sql", "true");
        
        // Enable performance monitoring
        PerformanceMonitor monitor = PerformanceMonitor.getInstance();
        monitor.setEnabled(true);
        monitor.setSlowQueryThreshold(100); // 100ms threshold
        
        // Create session factory
        SessionFactory sessionFactory = new SessionFactoryImpl(config);
        
        try {
            // Generate schema
            SchemaGenerator schemaGen = new SchemaGenerator(
                config.getDataSource(), 
                config.getDialect(), 
                ((SessionFactoryImpl) sessionFactory).getEntityMetadataMap()
            );
            schemaGen.createSchema();
            
            // Create sample data
            createSampleData(sessionFactory);
            
            // Demonstrate advanced querying
            demonstrateAdvancedQuerying(sessionFactory);
            
            // Demonstrate query builder
            demonstrateQueryBuilder(sessionFactory);
            
            // Show performance statistics
            monitor.logStats();
            
        } finally {
            sessionFactory.close();
        }
    }
    
    private static void createSampleData(SessionFactory sessionFactory) {
        System.out.println("=== Creating Sample Data ===");
        
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            try {
                // Create users
                User user1 = new User("alice", "alice@example.com");
                user1.setFirstName("Alice");
                user1.setLastName("Johnson");
                user1.setAge(28);
                
                User user2 = new User("bob", "bob@example.com");
                user2.setFirstName("Bob");
                user2.setLastName("Smith");
                user2.setAge(32);
                
                User user3 = new User("charlie", "charlie@example.com");
                user3.setFirstName("Charlie");
                user3.setLastName("Brown");
                user3.setAge(25);
                
                session.save(user1);
                session.save(user2);
                session.save(user3);
                
                // Create posts
                Post post1 = new Post("Getting Started with Java", "Java is a powerful programming language...", user1);
                Post post2 = new Post("Advanced ORM Techniques", "ORMs can greatly simplify database operations...", user1);
                Post post3 = new Post("Database Design Patterns", "Good database design is crucial for performance...", user2);
                Post post4 = new Post("Performance Optimization", "Here are some tips for optimizing your code...", user2);
                Post post5 = new Post("Code Review Best Practices", "Code reviews are essential for quality...", user3);
                
                session.save(post1);
                session.save(post2);
                session.save(post3);
                session.save(post4);
                session.save(post5);
                
                // Create comments
                Comment comment1 = new Comment("Great introduction!", "Dave", post1);
                Comment comment2 = new Comment("Very helpful, thanks!", "Eve", post1);
                Comment comment3 = new Comment("Interesting perspective", "Frank", post2);
                Comment comment4 = new Comment("I learned a lot from this", "Grace", post3);
                Comment comment5 = new Comment("Excellent examples", "Henry", post4);
                Comment comment6 = new Comment("Well written article", "Ivy", post5);
                
                session.save(comment1);
                session.save(comment2);
                session.save(comment3);
                session.save(comment4);
                session.save(comment5);
                session.save(comment6);
                
                tx.commit();
                System.out.println("Sample data created successfully!");
                
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
    
    private static void demonstrateAdvancedQuerying(SessionFactory sessionFactory) {
        System.out.println("\n=== Advanced Querying ===");
        
        try (Session session = sessionFactory.openSession()) {
            // Find users by age range using custom SQL
            System.out.println("Users aged 25-30:");
            List<User> youngUsers = session.createQuery(
                "SELECT * FROM users WHERE age BETWEEN 25 AND 30 ORDER BY age", 
                User.class
            );
            youngUsers.forEach(user -> 
                System.out.println("  " + user.getFirstName() + " " + user.getLastName() + " (age " + user.getAge() + ")")
            );
            
            // Find posts by user
            System.out.println("\nPosts by Alice:");
            List<Post> alicePosts = session.createQuery(
                "SELECT p.* FROM posts p JOIN users u ON p.user_id = u.id WHERE u.username = 'alice'", 
                Post.class
            );
            alicePosts.forEach(post -> 
                System.out.println("  " + post.getTitle())
            );
            
            // Count posts per user
            System.out.println("\nPost counts by user:");
            List<User> allUsers = session.findAll(User.class);
            for (User user : allUsers) {
                List<Post> userPosts = session.createQuery(
                    "SELECT * FROM posts WHERE user_id = " + user.getId(), 
                    Post.class
                );
                System.out.println("  " + user.getUsername() + ": " + userPosts.size() + " posts");
            }
        }
    }
    
    private static void demonstrateQueryBuilder(SessionFactory sessionFactory) {
        System.out.println("\n=== Query Builder Demo ===");
        
        try (Session session = sessionFactory.openSession()) {
            SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionFactory;
            
            // Example of how a query builder would work (simplified implementation)
            System.out.println("Finding active users with age > 25:");
            
            // This would be the ideal syntax for a query builder
            String sql = "SELECT * FROM users WHERE active = true AND age > 25 ORDER BY username";
            List<User> results = session.createQuery(sql, User.class);
            
            results.forEach(user -> 
                System.out.println("  " + user.getUsername() + " (" + user.getAge() + " years old)")
            );
            
            // Demonstrate pagination
            System.out.println("\nPaginated results (first 2 users):");
            String paginatedSql = "SELECT * FROM users ORDER BY username LIMIT 2";
            List<User> paginatedResults = session.createQuery(paginatedSql, User.class);
            
            paginatedResults.forEach(user -> 
                System.out.println("  " + user.getUsername())
            );
        }
    }
}
