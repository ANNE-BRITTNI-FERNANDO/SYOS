package com.syos.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.syos.infrastructure.database.ConnectionPool;
import com.syos.infrastructure.database.ConnectionPoolStats;
import com.syos.infrastructure.database.DatabaseConfig;

/**
 * Simple test class to verify database connectivity
 * Tests connection to XAMPP MySQL database
 */
public class SimpleConnectionTest {
    
    private final ConnectionPool connectionPool;
    
    public SimpleConnectionTest() {
        // Create database config with hardcoded values for testing
        DatabaseConfig config = new DatabaseConfig() {
            @Override
            public String getUrl() {
                return "jdbc:mysql://localhost:3306/syos_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            }
            
            @Override
            public String getUsername() {
                return "syos_user";
            }
            
            @Override
            public String getPassword() {
                return "temp1234";
            }
            
            @Override
            public String getDriverClassName() {
                return "com.mysql.cj.jdbc.Driver";
            }
            
            @Override
            public int getInitialPoolSize() {
                return 5;
            }
            
            @Override
            public int getMaxPoolSize() {
                return 10;
            }
            
            @Override
            public int getMinIdleConnections() {
                return 2;
            }
            
            @Override
            public long getConnectionTimeout() {
                return 30000;
            }
            
            @Override
            public long getIdleTimeout() {
                return 600000;
            }
            
            @Override
            public long getMaxLifetime() {
                return 1800000;
            }
        };
        
        this.connectionPool = new ConnectionPool(config);
    }
    
    /**
     * Test basic database connectivity
     */
    public boolean testConnection() {
        System.out.println("=== SYOS Database Connection Test ===");
        System.out.println("Testing connection to: jdbc:mysql://localhost:3306/syos_db");
        System.out.println("Username: syos_user");
        
        try (Connection connection = connectionPool.getConnection()) {
            System.out.println("✓ Successfully obtained connection from pool");
            
            // Test connection validity
            if (connection.isValid(5)) {
                System.out.println("✓ Connection is valid");
            } else {
                System.out.println("✗ Connection is not valid");
                return false;
            }
            
            // Test basic query
            String testQuery = "SELECT 1 as test_value, NOW() as current_time";
            try (PreparedStatement stmt = connection.prepareStatement(testQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    int testValue = rs.getInt("test_value");
                    String currentTime = rs.getString("current_time");
                    System.out.println("✓ Test query executed successfully");
                    System.out.println("  Test value: " + testValue);
                    System.out.println("  Current time: " + currentTime);
                } else {
                    System.out.println("✗ Test query returned no results");
                    return false;
                }
            }
            
            // Test database metadata
            String databaseName = connection.getCatalog();
            String databaseVersion = connection.getMetaData().getDatabaseProductVersion();
            System.out.println("✓ Database info retrieved:");
            System.out.println("  Database: " + databaseName);
            System.out.println("  Version: " + databaseVersion);
            
            return true;
            
        } catch (SQLException e) {
            System.out.println("✗ Database connection failed:");
            System.out.println("  Error: " + e.getMessage());
            System.out.println("  SQL State: " + e.getSQLState());
            System.out.println("  Error Code: " + e.getErrorCode());
            return false;
        }
    }
    
    /**
     * Test connection pool functionality
     */
    public void testConnectionPool() {
        System.out.println("\n=== Connection Pool Test ===");
        
        try {
            // Get pool statistics
            ConnectionPoolStats stats = connectionPool.getStats();
            System.out.println("Pool statistics: " + stats);
            
            // Test multiple connections
            System.out.println("Testing multiple connections...");
            for (int i = 1; i <= 3; i++) {
                try (Connection conn = connectionPool.getConnection()) {
                    System.out.println("✓ Connection " + i + " obtained successfully");
                    Thread.sleep(100); // Small delay
                }
            }
            
            // Get updated statistics
            stats = connectionPool.getStats();
            System.out.println("Updated pool statistics: " + stats);
            
        } catch (Exception e) {
            System.out.println("✗ Connection pool test failed: " + e.getMessage());
        }
    }
    
    /**
     * Main method to run the tests
     */
    public static void main(String[] args) {
        SimpleConnectionTest test = new SimpleConnectionTest();
        
        try {
            // Test basic connectivity
            boolean connectionSuccess = test.testConnection();
            
            if (connectionSuccess) {
                System.out.println("\n🎉 Database connection test PASSED!");
                
                // Test connection pool
                test.testConnectionPool();
                
            } else {
                System.out.println("\n❌ Database connection test FAILED!");
                System.out.println("\nTroubleshooting tips:");
                System.out.println("1. Make sure XAMPP is running");
                System.out.println("2. Check if MySQL service is started");
                System.out.println("3. Verify database 'syos_db' exists");
                System.out.println("4. Confirm user 'syos_user' exists with password 'temp1234'");
                System.out.println("5. Check if user has proper permissions");
            }
            
        } catch (Exception e) {
            System.out.println("\n💥 Test execution failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up
            test.connectionPool.close();
            System.out.println("\n=== Test completed ===");
        }
    }
}
