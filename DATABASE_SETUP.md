# SYOS Database Setup Guide

This guide will help you set up the MySQL database for the SYOS application using XAMPP.

## Prerequisites

1. **XAMPP installed** with MySQL
2. **Java JDK 11 or higher** (you have Java 24.0.1 ✓)
3. **XAMPP running** with MySQL service started

## Database Configuration

- **Database Name**: `syos_db`
- **Port**: `3306` (default MySQL port)
- **Username**: `syos_user`
- **Password**: `temp1234`

## Step-by-Step Setup

### 1. Start XAMPP

1. Open XAMPP Control Panel
2. Start **Apache** service (for phpMyAdmin)
3. Start **MySQL** service
4. Verify both services are running (green status)

### 2. Create Database and User

#### Option A: Using phpMyAdmin (Recommended)

1. Open your browser and go to: `http://localhost/phpmyadmin`
2. Click on "SQL" tab at the top
3. Copy and paste the contents of `src/main/resources/sql/syos_database_setup.sql`
4. Click "Go" to execute the script

#### Option B: Using MySQL Command Line

1. Open Command Prompt or PowerShell
2. Navigate to XAMPP MySQL bin directory (usually `C:\xampp\mysql\bin`)
3. Run: `mysql -u root -p`
4. Execute the SQL script:
   ```sql
   source "path\to\your\project\src\main\resources\sql\syos_database_setup.sql"
   ```

### 3. Verify Database Setup

After running the SQL script, you should see:
- Database `syos_db` created
- User `syos_user` created with password `temp1234`
- Test tables created with sample data

### 4. Test Database Connection

Run the automated connection test:

```batch
# Navigate to your project directory
cd "C:\Users\ASUS\Desktop\SE Y3 S1\CCCP 1\SYOS - Test"

# Run the test batch file
test-connection.bat
```

The test will:
1. Download MySQL JDBC connector automatically
2. Compile the test class
3. Test database connectivity
4. Create and query test tables
5. Display connection results

## Expected Test Output

If successful, you should see:
```
✓ MySQL JDBC driver loaded successfully
✓ Successfully connected to database
✓ Connection is valid
✓ Test query executed successfully
✓ Database info retrieved
✓ Test table created successfully
✓ Test data inserted
✓ Recent test records displayed
🎉 DATABASE CONNECTION TEST PASSED!
```

## Troubleshooting

### Common Issues and Solutions

#### 1. "MySQL JDBC driver not found"
- Ensure the test batch file downloaded the MySQL connector
- Check if `lib/mysql-connector-java-8.0.33.jar` exists

#### 2. "Connection refused"
- Verify XAMPP MySQL service is running
- Check if port 3306 is available
- Try restarting MySQL service in XAMPP

#### 3. "Access denied for user 'syos_user'"
- Run the database setup SQL script again
- Verify user was created with correct password
- Check user privileges in phpMyAdmin

#### 4. "Unknown database 'syos_db'"
- Run the database creation SQL script
- Verify database exists in phpMyAdmin

#### 5. "Communications link failure"
- Check Windows Firewall settings
- Ensure MySQL is listening on localhost:3306

### Manual Database Creation

If the automated script fails, create manually:

1. **Create Database**:
   ```sql
   CREATE DATABASE syos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Create User**:
   ```sql
   CREATE USER 'syos_user'@'localhost' IDENTIFIED BY 'temp1234';
   GRANT ALL PRIVILEGES ON syos_db.* TO 'syos_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

## Next Steps

Once the database connection test passes:

1. **For Maven users**: Install Maven and run `mvn spring-boot:run`
2. **For manual compilation**: Use the provided batch files
3. **Start development**: Begin implementing your SYOS application features

## Configuration Files

- **Database config**: `src/main/resources/config.properties`
- **Connection settings**: Modify these if you change database credentials
- **Spring Boot**: Full configuration in `DatabaseConfig.java`

## Security Notes

⚠️ **Important**: The password `temp1234` is for development only. 
For production:
1. Use a strong, unique password
2. Create environment variables for credentials
3. Use connection encryption (SSL)
4. Limit user privileges to minimum required

## Files Created

- `src/main/resources/sql/syos_database_setup.sql` - Database setup script
- `src/main/java/com/syos/test/SimpleDatabaseTest.java` - Connection test
- `test-connection.bat` - Automated test runner
- `pom.xml` - Maven dependencies (Spring Boot + MySQL)
- `src/main/resources/config.properties` - Database configuration

---

**Need help?** Check the console output from the test for specific error messages and troubleshooting suggestions.
