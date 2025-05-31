package nl.thedutchruben.databaseAnotations.demo;


import nl.thedutchruben.databaseAnotations.orm.core.Configuration;
import nl.thedutchruben.databaseAnotations.orm.core.SchemaGenerator;
import nl.thedutchruben.databaseAnotations.orm.migration.AddUserEmailIndexMigration;
import nl.thedutchruben.databaseAnotations.orm.migration.MigrationManager;
import nl.thedutchruben.databaseAnotations.orm.session.SessionFactory;
import nl.thedutchruben.databaseAnotations.orm.session.SessionFactoryImpl;

/**
 * Demo showing how to use the migration system.
 */
public class MigrationDemo {
    
    public static void main(String[] args) {
        // Configure the ORM
        Configuration config = new Configuration()
                .database("jdbc:h2:mem:migrationdb", "sa", "")
                .addEntity(User.class)
                .addEntity(Post.class)
                .addEntity(Comment.class)
                .setProperty("orm.show_sql", "true");
        
        // Create session factory
        SessionFactory sessionFactory = new SessionFactoryImpl(config);
        
        try {
            // Generate initial schema
            System.out.println("=== Creating Initial Schema ===");
            SchemaGenerator schemaGen = new SchemaGenerator(
                config.getDataSource(), 
                config.getDialect(), 
                ((SessionFactoryImpl) sessionFactory).getEntityMetadataMap()
            );
            schemaGen.createSchema();
            
            // Run migrations
            System.out.println("\n=== Running Migrations ===");
            MigrationManager migrationManager = new MigrationManager(config.getDataSource());
            
            // Add migrations in order
            migrationManager.addMigration(new AddUserEmailIndexMigration());
            migrationManager.addMigration(new AddPerformanceIndexesMigration());
            
            // Execute migrations
            migrationManager.migrate();
            
            System.out.println("\n=== Migrations completed successfully! ===");
            
        } finally {
            sessionFactory.close();
        }
    }
}
