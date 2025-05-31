# Database Annotation ORM - Project Structure

```
database-annotation/
├── pom.xml                             # Maven build configuration
├── README.md                           # Project documentation
├── build.sh                            # Linux/Mac build script
├── build.bat                           # Windows build script  
│
├── src/main/
│   ├── java/com/example/
│   │   ├── orm/                        # Core ORM Framework
│   │   │   ├── annotations/            # JPA-style annotations
│   │   │   │   ├── Entity.java
│   │   │   │   ├── Table.java
│   │   │   │   ├── Column.java
│   │   │   │   ├── Id.java
│   │   │   │   ├── GeneratedValue.java
│   │   │   │   ├── GenerationType.java
│   │   │   │   ├── OneToMany.java
│   │   │   │   ├── ManyToOne.java
│   │   │   │   ├── OneToOne.java
│   │   │   │   ├── JoinColumn.java
│   │   │   │   ├── CascadeType.java
│   │   │   │   └── FetchType.java
│   │   │   │
│   │   │   ├── core/                   # Core ORM functionality
│   │   │   │   ├── Configuration.java
│   │   │   │   ├── EntityMetadata.java
│   │   │   │   ├── ColumnMetadata.java
│   │   │   │   ├── RelationshipMetadata.java
│   │   │   │   ├── RelationshipType.java
│   │   │   │   └── SchemaGenerator.java
│   │   │   │
│   │   │   ├── dialect/               # Database-specific implementations
│   │   │   │   ├── Dialect.java
│   │   │   │   ├── MySQLDialect.java
│   │   │   │   ├── PostgreSQLDialect.java
│   │   │   │   └── SQLiteDialect.java
│   │   │   │
│   │   │   ├── session/               # Session management
│   │   │   │   ├── Session.java
│   │   │   │   ├── SessionImpl.java
│   │   │   │   ├── SessionFactory.java
│   │   │   │   ├── SessionFactoryImpl.java
│   │   │   │   ├── Transaction.java
│   │   │   │   └── TransactionImpl.java
│   │   │   │
│   │   │   ├── query/                 # Query building and execution
│   │   │   │   ├── Query.java
│   │   │   │   ├── QueryImpl.java
│   │   │   │   └── QueryBuilder.java
│   │   │   │
│   │   │   ├── exception/             # Custom exception classes
│   │   │   │   ├── ORMException.java
│   │   │   │   ├── EntityNotFoundException.java
│   │   │   │   ├── MappingException.java
│   │   │   │   ├── PersistenceException.java
│   │   │   │   ├── QueryException.java
│   │   │   │   └── TransactionException.java
│   │   │   │
│   │   │   ├── util/                  # Utility classes
│   │   │   │   ├── StringUtils.java
│   │   │   │   ├── ReflectionUtils.java
│   │   │   │   ├── TypeUtils.java
│   │   │   │   └── PerformanceMonitor.java
│   │   │   │
│   │   │   └── migration/             # Database migration system
│   │   │       ├── Migration.java
│   │   │       ├── MigrationManager.java
│   │   │       └── AddUserEmailIndexMigration.java
│   │   │
│   │   └── demo/                      # Demo applications and entities
│   │       ├── User.java              # Example entity with annotations
│   │       ├── Post.java              # Example entity with relationships
│   │       ├── Comment.java           # Example entity
│   │       ├── ORMDemo.java           # Basic CRUD demo
│   │       ├── AdvancedORMDemo.java   # Advanced features demo
│   │       ├── MigrationDemo.java     # Migration system demo
│   │       └── AddPerformanceIndexesMigration.java
│   │
│   └── resources/
│       ├── logback.xml                # Logging configuration
│       ├── orm.properties             # Default ORM configuration
│       ├── orm-dev.properties         # Development configuration
│       └── orm-prod.properties        # Production configuration
│
└── src/test/
    ├── java/com/example/orm/
    │   ├── ORMIntegrationTest.java     # Integration tests
    │   └── ORMUtilityTest.java         # Unit tests for utilities
    │
    └── resources/
        └── orm-test.properties         # Test configuration

```

## Key Components

### Core Framework (`src/main/java/com/example/orm/`)

1. **Annotations** - JPA-style annotations for entity mapping
2. **Core** - Entity metadata parsing and configuration management
3. **Dialect** - Database-specific SQL generation and feature support
4. **Session** - Session and transaction management with connection pooling
5. **Query** - Query building and execution framework
6. **Exception** - Comprehensive error handling with specific exceptions
7. **Util** - Utility classes for common operations
8. **Migration** - Database schema migration system

### Demo Applications (`src/main/java/com/example/demo/`)

1. **Entity Classes** - Example entities showing annotation usage
2. **Demo Applications** - Working examples of ORM features
3. **Migration Examples** - Database migration implementations

### Configuration (`src/main/resources/`)

1. **Environment-specific** configurations for dev/test/prod
2. **Logging** configuration with different levels
3. **Default** ORM properties and settings

### Tests (`src/test/`)

1. **Integration Tests** - End-to-end functionality testing
2. **Unit Tests** - Component-level testing
3. **Test Configuration** - Test-specific settings

## Build Artifacts

- **JAR file** - Packaged ORM library
- **Documentation** - JavaDoc and user guides
- **Examples** - Working demo applications
- **Tests** - Comprehensive test suite

## Usage

1. Run `build.sh` (Linux/Mac) or `build.bat` (Windows) to build and test
2. Use demo applications to see ORM features in action
3. Integrate the ORM into your own projects using the provided examples
4. Configure environment-specific properties as needed
5. Use migration system for database schema evolution

This structure provides a complete, production-ready ORM framework with comprehensive examples, documentation, and tooling.
