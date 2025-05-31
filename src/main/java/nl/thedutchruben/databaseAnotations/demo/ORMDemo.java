package nl.thedutchruben.databaseAnotations.demo;


import nl.thedutchruben.databaseAnotations.orm.core.Configuration;
import nl.thedutchruben.databaseAnotations.orm.core.SchemaGenerator;
import nl.thedutchruben.databaseAnotations.orm.session.Session;
import nl.thedutchruben.databaseAnotations.orm.session.SessionFactory;
import nl.thedutchruben.databaseAnotations.orm.session.SessionFactoryImpl;
import nl.thedutchruben.databaseAnotations.orm.session.Transaction;

import java.util.List;

/**
 * Demo application showing how to use the ORM framework.
 */
public class ORMDemo {
    
    public static void main(String[] args) {
        // Configure the ORM
        Configuration config = new Configuration()
                .database("jdbc:h2:mem:testdb", "sa", "")
                .addEntity(User.class)
                .addEntity(Post.class)
                .addEntity(Comment.class)
                .setProperty("orm.show_sql", "true");
        
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
            
            // Demo CRUD operations
            demoCRUDOperations(sessionFactory);
            
        } finally {
            sessionFactory.close();
        }
    }
    
    private static void demoCRUDOperations(SessionFactory sessionFactory) {
        // CREATE operations
        System.out.println("=== CREATE Operations ===");
        
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            try {
                // Create users
                User user1 = new User("john_doe", "john@example.com");
                user1.setFirstName("John");
                user1.setLastName("Doe");
                user1.setAge(30);
                
                User user2 = new User("jane_smith", "jane@example.com");
                user2.setFirstName("Jane");
                user2.setLastName("Smith");
                user2.setAge(25);
                
                // Save users
                session.save(user1);
                session.save(user2);
                
                // Create posts
                Post post1 = new Post("My First Post", "This is the content of my first post.", user1);
                Post post2 = new Post("Java ORM Tutorial", "Learn how to build an ORM from scratch.", user1);
                Post post3 = new Post("Database Design", "Best practices for database design.", user2);
                
                // Save posts
                session.save(post1);
                session.save(post2);
                session.save(post3);
                
                // Create comments
                Comment comment1 = new Comment("Great post!", "Alice", post1);
                Comment comment2 = new Comment("Very informative", "Bob", post2);
                Comment comment3 = new Comment("Thanks for sharing", "Charlie", post2);
                
                // Save comments
                session.save(comment1);
                session.save(comment2);
                session.save(comment3);
                
                tx.commit();
                System.out.println("Data created successfully!");
                
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
        
        // READ operations
        System.out.println("\n=== READ Operations ===");
        
        try (Session session = sessionFactory.openSession()) {
            // Find all users
            List<User> users = session.findAll(User.class);
            System.out.println("All Users:");
            users.forEach(System.out::println);
            
            // Find user by ID
            User user = session.findById(User.class, 1L);
            System.out.println("\nUser with ID 1: " + user);
            
            // Find all posts
            List<Post> posts = session.findAll(Post.class);
            System.out.println("\nAll Posts:");
            posts.forEach(System.out::println);
            
            // Find all comments
            List<Comment> comments = session.findAll(Comment.class);
            System.out.println("\nAll Comments:");
            comments.forEach(System.out::println);
        }
        
        // UPDATE operations
        System.out.println("\n=== UPDATE Operations ===");
        
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            try {
                // Update user
                User user = session.findById(User.class, 1L);
                if (user != null) {
                    user.setAge(31);
                    session.update(user);
                    System.out.println("Updated user: " + user);
                }
                
                // Update post
                Post post = session.findById(Post.class, 1L);
                if (post != null) {
                    post.setTitle("My Updated First Post");
                    session.update(post);
                    System.out.println("Updated post: " + post);
                }
                
                tx.commit();
                
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
        
        // DELETE operations
        System.out.println("\n=== DELETE Operations ===");
        
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            try {
                // Delete a comment
                Comment comment = session.findById(Comment.class, 3L);
                if (comment != null) {
                    session.delete(comment);
                    System.out.println("Deleted comment with ID: 3");
                }
                
                tx.commit();
                
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
        
        // Verify deletion
        try (Session session = sessionFactory.openSession()) {
            List<Comment> remainingComments = session.findAll(Comment.class);
            System.out.println("Remaining comments after deletion:");
            remainingComments.forEach(System.out::println);
        }
        
        System.out.println("\n=== Demo completed successfully! ===");
    }
}
