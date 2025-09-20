package syos.database;

import java.sql.Connection;
import java.sql.Statement;

import com.syos.infrastructure.database.ConnectionPool;

/**
 * Adds missing salt column to users table
 */
public class AddSaltColumn {
    
    public static void main(String[] args) {
        ConnectionPool connectionPool = null;
        try {
            connectionPool = new ConnectionPool();
            
            try (Connection conn = connectionPool.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                // Add salt column to users table
                String addSaltColumn = "ALTER TABLE users ADD COLUMN salt VARCHAR(255) AFTER password_hash";
                stmt.executeUpdate(addSaltColumn);
                System.out.println("✓ Added salt column to users table");
                
                // Check if the column was added successfully
                String checkColumn = "DESCRIBE users";
                var rs = stmt.executeQuery(checkColumn);
                System.out.println("\nUsers table structure:");
                while (rs.next()) {
                    System.out.println("- " + rs.getString("Field") + " (" + rs.getString("Type") + ")");
                }
                
            }
            
        } catch (Exception e) {
            System.err.println("Error adding salt column: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connectionPool != null) {
                connectionPool.close();
            }
        }
    }
}