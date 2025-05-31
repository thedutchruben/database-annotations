# Database Annotation ORM

A lightweight Java ORM framework with annotation support for MySQL, PostgreSQL, and SQLite databases.

## Features

- **Annotation-based mapping**: Use familiar JPA-style annotations like `@Entity`, `@Table`, `@Column`, `@Id`, etc.
- **Multi-database support**: Works with MySQL, PostgreSQL, SQLite, and H2
- **CRUD operations**: Save, update, delete, and find operations
- **Relationship mapping**: Support for `@OneToMany`, `@ManyToOne`, and `@OneToOne` relationships
- **Transaction management**: Programmatic transaction control
- **Connection pooling**: Built-in HikariCP connection pooling
- **Schema generation**: Automatic table creation from entity classes
- **Session management**: First-level caching and session lifecycle management

## Quick Start

### 1. Define Entity Classes

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();
    
    // Constructor, getters, setters...
}

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // Constructor, getters, setters...
}
```

### 2. Configure the ORM

```java
Configuration config = new Configuration()
    .database("jdbc:mysql://localhost:3306/mydb", "username", "password")
    .addEntity(User.class)
    .addEntity(Post.class)
    .setProperty("orm.show_sql", "true");

SessionFactory sessionFactory = new SessionFactoryImpl(config);
```

### 3. Generate Database Schema

```java
SchemaGenerator schemaGen = new SchemaGenerator(
    config.getDataSource(), 
    config.getDialect(), 
    ((SessionFactoryImpl) sessionFactory).getEntityMetadataMap()
);
schemaGen.createSchema();
```

### 4. Perform CRUD Operations

```java
try (Session session = sessionFactory.openSession()) {
    Transaction tx = session.beginTransaction();
    
    try {
        // Create
        User user = new User("john_doe", "john@example.com");
        session.save(user);
        
        // Read
        User foundUser = session.findById(User.class, 1L);
        List<User> allUsers = session.findAll(User.class);
        
        // Update
        foundUser.setEmail("newemail@example.com");
        session.update(foundUser);
        
        // Delete
        session.delete(foundUser);
        
        tx.commit();
    } catch (Exception e) {
        tx.rollback();
        throw e;
    }
}
```

## Supported Annotations

### Entity Annotations
- `@Entity`: Marks a class as a database entity
- `@Table`: Specifies table name, schema, and catalog

### Column Annotations
- `@Column`: Maps a field to a database column
- `@Id`: Marks a field as the primary key
- `@GeneratedValue`: Configures primary key generation strategy

### Relationship Annotations
- `@OneToMany`: One-to-many relationship
- `@ManyToOne`: Many-to-one relationship
- `@OneToOne`: One-to-one relationship
- `@JoinColumn`: Specifies foreign key column

## Database Support

### MySQL
```java
Configuration config = new Configuration()
    .database("jdbc:mysql://localhost:3306/mydb", "user", "pass");
```

### PostgreSQL
```java
Configuration config = new Configuration()
    .database("jdbc:postgresql://localhost:5432/mydb", "user", "pass");
```

### SQLite
```java
Configuration config = new Configuration()
    .database("jdbc:sqlite:mydb.db", "", "");
```

### H2 (In-Memory)
```java
Configuration config = new Configuration()
    .database("jdbc:h2:mem:mydb", "sa", "");
```

## Configuration Properties

```java
config.setProperty("orm.show_sql", "true");        // Show SQL statements
config.setProperty("orm.format_sql", "true");      // Format SQL output
config.setProperty("orm.hbm2ddl.auto", "create");  // Schema generation mode
```

## Schema Generation Modes

- `none`: No automatic schema management
- `create`: Create tables (drop existing)
- `update`: Update existing schema
- `validate`: Validate schema matches entities

## Transaction Management

```java
try (Session session = sessionFactory.openSession()) {
    Transaction tx = session.beginTransaction();
    
    try {
        // Your operations here
        session.save(entity);
        
        tx.commit();
    } catch (Exception e) {
        tx.rollback();
        throw e;
    }
}
```

## Building and Running

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Build the Project
```bash
mvn clean compile
```

### Run the Demo
```bash
mvn exec:java -Dexec.mainClass="com.example.demo.ORMDemo"
```

### Run Tests
```bash
mvn test
```

## Architecture Overview

The ORM framework consists of several key components:

- **Annotations**: Define entity metadata using runtime annotations
- **Entity Metadata**: Parses and caches entity structure information
- **Session Management**: Handles database sessions and first-level caching
- **Transaction Management**: Provides programmatic transaction control
- **Dialect System**: Database-specific SQL generation and feature support
- **Schema Generation**: Creates database tables from entity definitions
- **Connection Pooling**: Efficient database connection management

## Best Practices

1. **Always use transactions** for data modifications
2. **Close sessions properly** using try-with-resources
3. **Use lazy loading** for collections to avoid N+1 queries
4. **Maintain bidirectional relationships** properly
5. **Add proper indexes** for foreign key columns
6. **Use connection pooling** in production environments

## Limitations

- No query caching beyond session scope
- Limited support for complex queries (use native SQL)
- No automatic dirty checking (must call update explicitly)
- No support for inheritance mapping strategies
- No support for composite primary keys

## Future Enhancements

- Second-level caching support
- Query builder with type safety
- Migration framework integration
- Support for stored procedures
- Criteria API for dynamic queries
- Support for database views
- Automatic schema migration

## License

This project is open source and available under the MIT License.
