package com.syos.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple database connection test without Spring Boot dependencies
 * Tests direct JDBC connectivity to XAMPP MySQL database
 */
public class SimpleDatabaseTest {
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/syos_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USERNAME = "syos_user";
    private static final String DB_PASSWORD = "temp1234";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /**
     * Test basic database connectivity
     */
    public static boolean testConnection() {
        System.out.println("=== SYOS Database Connection Test ===");
        System.out.println("Testing connection to: " + DB_URL);
        System.out.println("Username: " + DB_USERNAME);
        System.out.println();
        
        try {
            // Load MySQL JDBC driver
            Class.forName(DB_DRIVER);
            System.out.println("✓ MySQL JDBC driver loaded successfully");
            
            // Establish connection
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                System.out.println("✓ Successfully connected to database");
                
                // Test connection validity
                if (connection.isValid(5)) {
                    System.out.println("✓ Connection is valid");
                } else {
                    System.out.println("✗ Connection is not valid");
                    return false;
                }
                
                // Test basic query (MariaDB/MySQL compatible)
                String testQuery = "SELECT 1, NOW()";
                try (PreparedStatement stmt = connection.prepareStatement(testQuery);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    if (rs.next()) {
                        int testValue = rs.getInt(1);
                        String currentTime = rs.getString(2);
                        
                        System.out.println("✓ Test query executed successfully");
                        System.out.println("  Test value: " + testValue);
                        System.out.println("  Current time: " + currentTime);
                    } else {
                        System.out.println("✗ Test query returned no results");
                        return false;
                    }
                }
                
                // Test database metadata
                String databaseVersion = connection.getMetaData().getDatabaseProductVersion();
                String databaseProduct = connection.getMetaData().getDatabaseProductName();
                System.out.println("✓ Database info retrieved:");
                System.out.println("  Product: " + databaseProduct);
                System.out.println("  Version: " + databaseVersion);
                
                // Test table creation (optional)
                testTableOperations(connection);
                
                return true;
                
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("✗ MySQL JDBC driver not found:");
            System.out.println("  Error: " + e.getMessage());
            System.out.println("  Please ensure mysql-connector-java JAR is in classpath");
            return false;
            
        } catch (SQLException e) {
            System.out.println("✗ Database connection failed:");
            System.out.println("  Error: " + e.getMessage());
            System.out.println("  SQL State: " + e.getSQLState());
            System.out.println("  Error Code: " + e.getErrorCode());
            
            // Provide specific troubleshooting based on error
            provideTroubleshootingAdvice(e);
            return false;
        }
    }
    
    /**
     * Test basic table operations
     */
    private static void testTableOperations(Connection connection) {
        System.out.println("\n--- Testing Table Operations ---");
        
        try {
            // Create a test table
            String createTableSQL = "CREATE TABLE IF NOT EXISTS connection_test (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "test_message VARCHAR(255), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            
            try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
                stmt.executeUpdate();
                System.out.println("✓ Test table created successfully");
            }
            
            // Insert test data
            String insertSQL = "INSERT INTO connection_test (test_message) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                stmt.setString(1, "SYOS Connection Test - " + java.time.LocalDateTime.now());
                int rowsAffected = stmt.executeUpdate();
                System.out.println("✓ Test data inserted (" + rowsAffected + " row affected)");
            }
            
            // Query test data
            String selectSQL = "SELECT * FROM connection_test ORDER BY created_at DESC LIMIT 3";
            try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
                 ResultSet rs = stmt.executeQuery()) {
                
                System.out.println("✓ Recent test records:");
                while (rs.next()) {
                    System.out.println("  ID: " + rs.getInt("id") + 
                                     ", Message: " + rs.getString("test_message") + 
                                     ", Created: " + rs.getTimestamp("created_at"));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("⚠ Table operations test failed: " + e.getMessage());
            System.out.println("  This might indicate insufficient database permissions");
        }
    }
    
    /**
     * Provide troubleshooting advice based on SQL exception
     */
    private static void provideTroubleshootingAdvice(SQLException e) {
        System.out.println("\nTroubleshooting suggestions:");
        
        int errorCode = e.getErrorCode();
        String sqlState = e.getSQLState();
        
        if (errorCode == 1045 || "28000".equals(sqlState)) {
            System.out.println("• Authentication failed - check username/password");
            System.out.println("• Verify user 'syos_user' exists in MySQL");
            System.out.println("• Check password is 'temp1234'");
        } else if (errorCode == 1049 || "42000".equals(sqlState)) {
            System.out.println("• Database 'syos_db' does not exist");
            System.out.println("• Create the database in MySQL/phpMyAdmin");
        } else if (errorCode == 0 || e.getMessage().contains("Connection refused")) {
            System.out.println("• MySQL server is not running");
            System.out.println("• Start XAMPP and ensure MySQL service is running");
            System.out.println("• Check if port 3306 is available");
        } else {
            System.out.println("• Check XAMPP MySQL configuration");
            System.out.println("• Verify firewall settings");
            System.out.println("• Ensure MySQL is listening on port 3306");
        }
    }
    
    /**
     * Main method to run the test
     */
    public static void main(String[] args) {
        System.out.println("SYOS Database Connection Test");
        System.out.println("============================\n");
        
        try {
            boolean success = testConnection();
            
            if (success) {
                System.out.println("\n🎉 DATABASE CONNECTION TEST PASSED!");
                System.out.println("✓ XAMPP MySQL is working correctly");
                System.out.println("✓ Database connectivity established");
                System.out.println("✓ Ready for SYOS application development");
            } else {
                System.out.println("\n❌ DATABASE CONNECTION TEST FAILED!");
                System.out.println("\nNext steps:");
                System.out.println("1. Start XAMPP Control Panel");
                System.out.println("2. Start MySQL service");
                System.out.println("3. Open phpMyAdmin");
                System.out.println("4. Create database 'syos_db'");
                System.out.println("5. Create user 'syos_user' with password 'temp1234'");
                System.out.println("6. Grant all privileges to syos_user on syos_db");
            }
            
        } catch (Exception e) {
            System.out.println("\n💥 Test execution failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Test completed ===");
    }
}