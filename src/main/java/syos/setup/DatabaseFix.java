package syos.setup;

import java.sql.Connection;
import java.sql.Statement;

import com.syos.infrastructure.database.ConnectionPool;

/**
 * Fix database table issues
 */
public class DatabaseFix {
    public static void main(String[] args) {
        try {
            ConnectionPool pool = new ConnectionPool();
            Connection conn = pool.getConnection();
            Statement stmt = conn.createStatement();
            
            // Fix operation column size
            stmt.executeUpdate("ALTER TABLE audit_logs MODIFY operation VARCHAR(100)");
            System.out.println("✓ Updated audit_logs operation column size");
            
            // Make last_name optional in users table
            stmt.executeUpdate("ALTER TABLE users MODIFY last_name VARCHAR(50) NULL");
            System.out.println("✓ Made last_name optional in users table");
            
            conn.close();
            pool.close();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}