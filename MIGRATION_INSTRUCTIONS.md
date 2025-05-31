# Database Annotation ORM - Package Migration Instructions

## 🚀 AUTOMATED MIGRATION SCRIPT

I've created a comprehensive PowerShell script that will automatically complete the entire package migration from `com.example` to `nl.thedutchruben.databaseAnotations`.

### ✨ Features

- **Complete automation** - Migrates all 25 remaining files
- **Safe operation** - Dry run mode to preview changes
- **Smart updates** - Updates package declarations and import statements  
- **Verification** - Automatic compilation test after migration
- **Cleanup option** - Removes old package structure when done
- **Detailed logging** - Color-coded progress reports

---

## 📋 USAGE INSTRUCTIONS

### Method 1: Quick Migration (Recommended)

Open PowerShell as Administrator in your project directory and run:

```powershell
# Navigate to project directory
cd "D:\code\database-annotation"

# Run the migration script
.\migrate-packages.ps1
```

### Method 2: Preview First (Safer)

```powershell
# Preview what changes will be made (no actual changes)
.\migrate-packages.ps1 -DryRun

# If preview looks good, run the actual migration
.\migrate-packages.ps1

# Clean up old package structure when everything works
.\migrate-packages.ps1 -Cleanup
```

### Method 3: All-in-One

```powershell
# Complete migration with immediate cleanup
.\migrate-packages.ps1 -Cleanup
```

---

## 🔧 SCRIPT PARAMETERS

| Parameter | Description | Example |
|-----------|-------------|---------|
| `-DryRun` | Preview changes without making them | `.\migrate-packages.ps1 -DryRun` |
| `-Cleanup` | Remove old package structure after migration | `.\migrate-packages.ps1 -Cleanup` |
| (none) | Standard migration | `.\migrate-packages.ps1` |

---

## 📊 WHAT THE SCRIPT WILL MIGRATE

### Main Source Files (20 files):
- **Exception Package** (6 files) - All exception classes
- **Migration Package** (3 files) - Migration system
- **Query Package** (3 files) - Query builder and execution
- **Session Package** (4 files) - Session factory and implementation
- **Util Package** (4 files) - Utility classes

### Demo Files (3 files):
- AddPerformanceIndexesMigration.java
- AdvancedORMDemo.java  
- MigrationDemo.java

### Test Files (2 files):
- ORMIntegrationTest.java
- ORMUtilityTest.java

**Total: 25 files**

---

## ✅ VERIFICATION STEPS

After running the script, verify the migration:

### 1. Compilation Test
```bash
mvn clean compile
```

### 2. Run Tests
```bash
mvn test
```

### 3. Test Demo Applications
```bash
# Basic demo
mvn exec:java -Dexec.mainClass="nl.thedutchruben.databaseAnotations.demo.ORMDemo"

# Advanced demo  
mvn exec:java -Dexec.mainClass="nl.thedutchruben.databaseAnotations.demo.AdvancedORMDemo"

# Migration demo
mvn exec:java -Dexec.mainClass="nl.thedutchruben.databaseAnotations.demo.MigrationDemo"
```

---

## 🚨 TROUBLESHOOTING

### PowerShell Execution Policy Error
If you get an execution policy error:

```powershell
# Allow scripts for current session
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Then run the migration script
.\migrate-packages.ps1
```

### Script Not Found Error
Make sure you're in the correct directory:

```powershell
# Check current directory
pwd

# Should show: D:\code\database-annotation

# If not, navigate there
cd "D:\code\database-annotation"
```

### Compilation Errors After Migration
If compilation fails after migration:

1. **Run with DryRun first** to check for issues:
   ```powershell
   .\migrate-packages.ps1 -DryRun
   ```

2. **Check remaining import statements** manually

3. **Re-run without cleanup** to keep old files as backup:
   ```powershell
   .\migrate-packages.ps1
   ```

---

## 🧹 AFTER SUCCESSFUL MIGRATION

Once everything is working:

1. **Remove old packages**:
   ```powershell
   .\migrate-packages.ps1 -Cleanup
   ```

2. **Update your IDE** - Refresh/reimport the project

3. **Update documentation** that references old package names

4. **Commit changes** to version control

---

## 📁 EXPECTED RESULT

After successful migration, your package structure will be:

```
src/main/java/nl/thedutchruben/databaseAnotations/
├── demo/
│   ├── User.java ✅
│   ├── Post.java ✅  
│   ├── Comment.java ✅
│   ├── ORMDemo.java ✅
│   ├── AdvancedORMDemo.java ✅
│   ├── MigrationDemo.java ✅
│   └── AddPerformanceIndexesMigration.java ✅
└── orm/
    ├── annotations/ ✅ (12 files)
    ├── core/ ✅ (6 files)
    ├── dialect/ ✅ (5 files)
    ├── exception/ ✅ (6 files)
    ├── migration/ ✅ (3 files)
    ├── query/ ✅ (3 files)
    ├── session/ ✅ (6 files)
    └── util/ ✅ (4 files)
```

**Total: 54 files in correct package structure** 🎉

---

## 🎯 READY TO RUN?

Execute this command to start the migration:

```powershell
cd "D:\code\database-annotation"
.\migrate-packages.ps1
```

The script will handle everything automatically and provide detailed progress updates!
