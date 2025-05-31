# Package Migration Guide: com.example → nl.thedutchruben.databaseAnotations

## Overview
All classes need to be moved from `com.example.*` to `nl.thedutchruben.databaseAnotations.*` packages with updated import statements.

## Directory Structure to Create
```
src/main/java/nl/thedutchruben/databaseAnotations/
├── demo/
├── orm/
│   ├── annotations/ (✓ Already exists)
│   ├── core/ (✓ Completed)
│   ├── dialect/ (✓ Completed)
│   ├── exception/
│   ├── migration/
│   ├── query/
│   ├── session/
│   └── util/
```

## Files Completed
✓ All demo entity classes (User.java, Post.java, Comment.java)
✓ All core classes (Configuration.java, ColumnMetadata.java, EntityMetadata.java, etc.)
✓ All dialect classes (Dialect.java, H2Dialect.java, MySQLDialect.java, etc.)

## Remaining Files to Move

### Exception Package
1. EntityNotFoundException.java
2. MappingException.java
3. ORMException.java
4. PersistenceException.java
5. QueryException.java
6. TransactionException.java

### Migration Package
1. Migration.java
2. MigrationManager.java
3. AddUserEmailIndexMigration.java

### Query Package
1. Query.java
2. QueryBuilder.java
3. QueryImpl.java

### Session Package
1. Session.java
2. SessionFactory.java
3. SessionFactoryImpl.java
4. SessionImpl.java
5. Transaction.java
6. TransactionImpl.java

### Util Package
1. PerformanceMonitor.java
2. ReflectionUtils.java
3. StringUtils.java
4. TypeUtils.java

### Demo Classes
1. ORMDemo.java
2. AdvancedORMDemo.java
3. MigrationDemo.java
4. AddPerformanceIndexesMigration.java

### Test Classes
1. ORMIntegrationTest.java
2. ORMUtilityTest.java

## Import Statement Changes Required

### Original Import Pattern
```java
import com.example.orm.core.*;
import com.example.orm.annotations.*;
import com.example.orm.session.*;
import com.example.demo.*;
```

### New Import Pattern
```java
import nl.thedutchruben.databaseAnotations.orm.core.*;
import nl.thedutchruben.databaseAnotations.orm.annotations.*;
import nl.thedutchruben.databaseAnotations.orm.session.*;
import nl.thedutchruben.databaseAnotations.demo.*;
```

## Automated Migration Script (PowerShell)

```powershell
# Create directory structure
$basePath = "D:\code\database-annotation\src\main\java\nl\thedutchruben\databaseAnotations\orm"
New-Item -Path "$basePath\exception" -ItemType Directory -Force
New-Item -Path "$basePath\migration" -ItemType Directory -Force
New-Item -Path "$basePath\query" -ItemType Directory -Force
New-Item -Path "$basePath\session" -ItemType Directory -Force
New-Item -Path "$basePath\util" -ItemType Directory -Force

# Function to update package and imports in Java files
function Update-JavaPackage {
    param($sourcePath, $targetPath, $newPackage)
    
    $content = Get-Content $sourcePath -Raw
    
    # Update package declaration
    $content = $content -replace "^package com\.example\.", "package $newPackage."
    
    # Update import statements
    $content = $content -replace "import com\.example\.orm\.", "import nl.thedutchruben.databaseAnotations.orm."
    $content = $content -replace "import com\.example\.demo\.", "import nl.thedutchruben.databaseAnotations.demo."
    
    Set-Content -Path $targetPath -Value $content
}

# Example usage for exception package
$sourceFiles = @(
    "EntityNotFoundException.java",
    "MappingException.java",
    "ORMException.java",
    "PersistenceException.java",
    "QueryException.java",
    "TransactionException.java"
)

foreach ($file in $sourceFiles) {
    $sourcePath = "D:\code\database-annotation\src\main\java\com\example\orm\exception\$file"
    $targetPath = "D:\code\database-annotation\src\main\java\nl\thedutchruben\databaseAnotations\orm\exception\$file"
    Update-JavaPackage -sourcePath $sourcePath -targetPath $targetPath -newPackage "nl.thedutchruben.databaseAnotations.orm.exception"
}
```

## Manual Steps (Alternative)

1. **Create remaining directories** using the file manager or commands
2. **Copy and modify each file** by:
   - Copying the file to the new location
   - Updating the package declaration at the top
   - Updating all import statements
3. **Update build scripts** and configuration files
4. **Run tests** to verify everything works

## After Migration Cleanup

1. Delete the old `com/example` directory structure
2. Update any documentation or configuration files that reference the old packages
3. Update the main class references in build scripts
4. Run a full build and test to ensure everything works

## Quick Test Command
```bash
# Compile to check for errors
mvn clean compile

# Run tests
mvn test

# Run demos
mvn exec:java -Dexec.mainClass="nl.thedutchruben.databaseAnotations.demo.ORMDemo"
```

Would you like me to continue with the manual migration of specific packages, or would you prefer to use the automated script approach?
