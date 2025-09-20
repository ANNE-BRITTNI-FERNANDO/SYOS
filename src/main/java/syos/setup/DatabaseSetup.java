package syos.setup;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.syos.infrastructure.database.ConnectionPool;

/**
 * Database setup utility to create tables and initial data
 */
public class DatabaseSetup {
    
    private final ConnectionPool connectionPool;
    
    public DatabaseSetup() {
        this.connectionPool = new ConnectionPool();
    }
    
    public void setupDatabase() {
        System.out.println("Setting up SYOS database...");
        
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            
            // Create roles table
            createRolesTable(statement);
            
            // Create users table (simplified for authentication)
            createUsersTable(statement);
            
            // Create audit_logs table
            createAuditLogsTable(statement);
            
            // Insert default roles
            insertDefaultRoles(statement);
            
            System.out.println("✓ Database setup completed successfully!");
            
        } catch (SQLException e) {
            System.err.println("✗ Database setup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createRolesTable(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS roles (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "role_name VARCHAR(50) UNIQUE NOT NULL," +
                     "description TEXT," +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                     "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                     ")";
        
        statement.executeUpdate(sql);
        System.out.println("✓ Roles table created");
    }
    
    private void createUsersTable(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "username VARCHAR(50) UNIQUE NOT NULL," +
                     "password_hash VARCHAR(255) NOT NULL," +
                     "email VARCHAR(100) UNIQUE NOT NULL," +
                     "first_name VARCHAR(50) NOT NULL," +
                     "last_name VARCHAR(50) NOT NULL," +
                     "phone_number VARCHAR(20)," +
                     "role_id INT," +
                     "is_active BOOLEAN DEFAULT TRUE," +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                     "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                     "FOREIGN KEY (role_id) REFERENCES roles(id)" +
                     ")";
        
        statement.executeUpdate(sql);
        System.out.println("✓ Users table created");
    }
    
    private void createAuditLogsTable(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS audit_logs (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "table_name VARCHAR(50) NOT NULL," +
                     "operation VARCHAR(20) NOT NULL," +
                     "record_id INT," +
                     "old_values TEXT," +
                     "new_values TEXT," +
                     "user_id INT," +
                     "ip_address VARCHAR(45)," +
                     "user_agent TEXT," +
                     "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                     "FOREIGN KEY (user_id) REFERENCES users(id)" +
                     ")";
        
        statement.executeUpdate(sql);
        System.out.println("✓ Audit logs table created");
    }
    
    private void insertDefaultRoles(Statement statement) throws SQLException {
        // Insert USER role
        String sql1 = "INSERT IGNORE INTO roles (role_name, description) " +
                      "VALUES ('USER', 'Standard user role')";
        statement.executeUpdate(sql1);
        
        // Insert MANAGER role
        String sql2 = "INSERT IGNORE INTO roles (role_name, description) " +
                      "VALUES ('MANAGER', 'Manager role with elevated privileges')";
        statement.executeUpdate(sql2);
        
        // Insert ADMIN role
        String sql3 = "INSERT IGNORE INTO roles (role_name, description) " +
                      "VALUES ('ADMIN', 'Administrator role with full access')";
        statement.executeUpdate(sql3);
        
        System.out.println("✓ Default roles inserted");
    }
    
    public static void main(String[] args) {
        try {
            DatabaseSetup setup = new DatabaseSetup();
            setup.setupDatabase();
        } catch (Exception e) {
            System.err.println("Failed to setup database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}