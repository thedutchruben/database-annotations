# API Documentation

## Core APIs

### Configuration API

#### Configuration Class
The `Configuration` class is the entry point for setting up the ORM framework.

```java
import nl.thedutchruben.databaseAnotations.orm.core.Configuration;

Configuration config = new Configuration();
```

**Key Methods:**
- `database(String url, String username, String password)` - Configure database connection
- `addEntity(Class<?> entityClass)` - Register entity class
- `setProperty(String key, String value)` - Set configuration property
- `getDataSource()` - Get configured data source
- `getDialect()` - Get database dialect

### Session API

#### Session Interface
Primary interface for database operations.

```java
import nl.thedutchruben.databaseAnotations.orm.session.Session;

try (Session session = sessionFactory.openSession()) {
    // Operations
}
```

**CRUD Methods:**
- `<T> T save(T entity)` - Insert new entity
- `<T> T update(T entity)` - Update existing entity
- `<T> T saveOrUpdate(T entity)` - Insert or update
- `<T> void delete(T entity)` - Delete entity
- `<T> T findById(Class<T> entityClass, Object id)` - Find by primary key
- `<T> List<T> findAll(Class<T> entityClass)` - Find all entities

**Query Methods:**
- `<T> List<T> createQuery(String sql, Class<T> resultClass)` - Execute custom SQL
- `int executeUpdate(String sql)` - Execute update/delete SQL

**Transaction Methods:**
- `Transaction beginTransaction()` - Start new transaction
- `Transaction getTransaction()` - Get current transaction

**Session Management:**
- `void flush()` - Flush pending changes
- `void clear()` - Clear session cache
- `boolean isOpen()` - Check if session is open
- `void close()` - Close session

#### SessionFactory Interface
Factory for creating sessions.

```java
import nl.thedutchruben.databaseAnotations.orm.session.SessionFactory;
import nl.thedutchruben.databaseAnotations.orm.session.SessionFactoryImpl;

SessionFactory sessionFactory = new SessionFactoryImpl(config);
```

**Methods:**
- `Session openSession()` - Create new session
- `Session getCurrentSession()` - Get thread-bound session
- `boolean isClosed()` - Check if factory is closed
- `void close()` - Close factory and release resources

### Transaction API

#### Transaction Interface
Manages database transactions.

```java
Transaction tx = session.beginTransaction();
try {
    // Operations
    tx.commit();
} catch (Exception e) {
    tx.rollback();
    throw e;
}
```

**Methods:**
- `void commit()` - Commit transaction
- `void rollback()` - Rollback transaction
- `boolean isActive()` - Check if transaction is active

### Schema Generation API

#### SchemaGenerator Class
Generates database schema from entity metadata.

```java
import nl.thedutchruben.databaseAnotations.orm.core.SchemaGenerator;

SchemaGenerator generator = new SchemaGenerator(dataSource, dialect, entityMetadataMap);
```

**Methods:**
- `void createSchema()` - Create all tables
- `void dropSchema()` - Drop all tables
- `void recreateSchema()` - Drop and recreate all tables

### Migration API

#### Migration Interface
Interface for database migrations.

```java
import nl.thedutchruben.databaseAnotations.orm.migration.Migration;

public class MyMigration implements Migration {
    @Override
    public String getVersion() { return "001_my_migration"; }
    
    @Override
    public String getDescription() { return "Description"; }
    
    @Override
    public void up(Connection connection) throws SQLException {
        // Migration logic
    }
    
    @Override
    public void down(Connection connection) throws SQLException {
        // Rollback logic
    }
}
```

#### MigrationManager Class
Manages and executes migrations.

```java
import nl.thedutchruben.databaseAnotations.orm.migration.MigrationManager;

MigrationManager manager = new MigrationManager(dataSource);
manager.addMigration(new MyMigration());
manager.migrate();
```

**Methods:**
- `void addMigration(Migration migration)` - Add migration
- `void migrate()` - Execute all pending migrations

## Annotation APIs

### Entity Annotations

#### @Entity
Marks a class as a database entity.

```java
@Entity
@Entity(name = "custom_table_name")
public class MyEntity {
    // ...
}
```

**Attributes:**
- `name` (optional) - Custom table name

#### @Table
Specifies table details.

```java
@Table(name = "my_table", schema = "my_schema", catalog = "my_catalog")
public class MyEntity {
    // ...
}
```

**Attributes:**
- `name` (required) - Table name
- `schema` (optional) - Database schema
- `catalog` (optional) - Database catalog

### Field Annotations

#### @Id
Marks primary key field.

```java
@Id
private Long id;
```

#### @GeneratedValue
Configures primary key generation.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

**Generation Strategies:**
- `GenerationType.AUTO` - Provider chooses strategy
- `GenerationType.IDENTITY` - Database identity column
- `GenerationType.SEQUENCE` - Database sequence
- `GenerationType.TABLE` - Database table

#### @Column
Maps field to database column.

```java
@Column(name = "user_name", nullable = false, unique = true, length = 50)
private String username;
```

**Attributes:**
- `name` - Column name (default: field name)
- `nullable` - Allow null values (default: true)
- `unique` - Unique constraint (default: false)
- `length` - Column length for strings (default: 255)
- `precision` - Numeric precision (default: 0)
- `scale` - Numeric scale (default: 0)
- `columnDefinition` - Custom column definition

### Relationship Annotations

#### @ManyToOne
Defines many-to-one relationship.

```java
@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

**Attributes:**
- `targetEntity` - Target entity class
- `cascade` - Cascade operations
- `fetch` - Fetch strategy (EAGER/LAZY)
- `optional` - Whether relationship is optional

#### @OneToMany
Defines one-to-many relationship.

```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Post> posts;
```

**Attributes:**
- `mappedBy` - Owning field name
- `targetEntity` - Target entity class
- `cascade` - Cascade operations
- `fetch` - Fetch strategy
- `optional` - Whether relationship is optional

#### @OneToOne
Defines one-to-one relationship.

```java
@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "profile_id")
private UserProfile profile;
```

**Attributes:**
- `mappedBy` - Owning field name
- `targetEntity` - Target entity class
- `cascade` - Cascade operations
- `fetch` - Fetch strategy
- `optional` - Whether relationship is optional

#### @JoinColumn
Specifies foreign key column.

```java
@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
```

**Attributes:**
- `name` - Foreign key column name
- `referencedColumnName` - Referenced column name
- `nullable` - Whether column allows nulls
- `unique` - Unique constraint
- `insertable` - Whether column is insertable
- `updatable` - Whether column is updatable

### Cascade Types

- `CascadeType.ALL` - All operations
- `CascadeType.PERSIST` - Save operations
- `CascadeType.MERGE` - Update operations
- `CascadeType.REMOVE` - Delete operations
- `CascadeType.REFRESH` - Refresh operations
- `CascadeType.DETACH` - Detach operations

### Fetch Types

- `FetchType.EAGER` - Load immediately
- `FetchType.LAZY` - Load on demand

## Utility APIs

### PerformanceMonitor

Monitor ORM performance and query execution times.

```java
import nl.thedutchruben.databaseAnotations.orm.util.PerformanceMonitor;

PerformanceMonitor monitor = PerformanceMonitor.getInstance();
monitor.setEnabled(true);
monitor.setSlowQueryThreshold(100); // 100ms

// Get statistics
PerformanceMonitor.PerformanceStats stats = monitor.getStats();
monitor.logStats();
```

### ReflectionUtils

Utility methods for reflection operations.

```java
import nl.thedutchruben.databaseAnotations.orm.util.ReflectionUtils;

List<Field> fields = ReflectionUtils.getAllFields(MyEntity.class);
Field idField = ReflectionUtils.getFieldWithAnnotation(MyEntity.class, Id.class);
MyEntity instance = ReflectionUtils.newInstance(MyEntity.class);
```

### StringUtils

Common string operations.

```java
import nl.thedutchruben.databaseAnotations.orm.util.StringUtils;

String snakeCase = StringUtils.camelToSnake("userName"); // "user_name"
String camelCase = StringUtils.snakeToCamel("user_name"); // "userName"
boolean empty = StringUtils.isEmpty(str);
```

### TypeUtils

Type conversion utilities.

```java
import nl.thedutchruben.databaseAnotations.orm.util.TypeUtils;

boolean isBasic = TypeUtils.isBasicType(String.class);
Class<?> wrapper = TypeUtils.getWrapperType(int.class); // Integer.class
Object converted = TypeUtils.convertForDatabase(value, targetType);
```

## Exception Handling

### Exception Hierarchy

```
ORMException (base)
├── EntityNotFoundException
├── MappingException
├── PersistenceException
├── QueryException
└── TransactionException
```

### Common Exceptions

#### EntityNotFoundException
Thrown when entity is not found.

```java
try {
    User user = session.findById(User.class, 999L);
} catch (EntityNotFoundException e) {
    // Handle missing entity
}
```

#### MappingException
Thrown for entity mapping issues.

```java
try {
    config.addEntity(InvalidEntity.class);
} catch (MappingException e) {
    // Handle mapping error
}
```

#### PersistenceException
Thrown for database operation failures.

```java
try {
    session.save(entity);
} catch (PersistenceException e) {
    // Handle persistence error
}
```

#### QueryException
Thrown for query execution errors.

```java
try {
    List<User> users = session.createQuery("INVALID SQL", User.class);
} catch (QueryException e) {
    // Handle query error
}
```

#### TransactionException
Thrown for transaction errors.

```java
try {
    transaction.commit();
} catch (TransactionException e) {
    // Handle transaction error
}
```

## Thread Safety

### Thread-Safe Components
- `SessionFactory` - Thread-safe, create once per application
- `Configuration` - Thread-safe after initialization
- `PerformanceMonitor` - Thread-safe singleton

### Non-Thread-Safe Components
- `Session` - Not thread-safe, use one per thread
- `Transaction` - Not thread-safe, tied to specific session
- Entity instances - Not thread-safe, don't share between threads

### Best Practices
```java
// Thread-safe: One SessionFactory per application
private static final SessionFactory sessionFactory = new SessionFactoryImpl(config);

// Thread-local sessions
private static final ThreadLocal<Session> sessionHolder = new ThreadLocal<>();

public static Session getCurrentSession() {
    Session session = sessionHolder.get();
    if (session == null || !session.isOpen()) {
        session = sessionFactory.openSession();
        sessionHolder.set(session);
    }
    return session;
}
```
