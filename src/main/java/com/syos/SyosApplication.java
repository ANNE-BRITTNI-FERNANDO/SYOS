package com.syos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.syos.infrastructure.database.ConnectionPool;
import com.syos.infrastructure.database.ConnectionPoolStats;

/**
 * Main Spring Boot application class for SYOS
 */
@SpringBootApplication
public class SyosApplication {

    public static void main(String[] args) {
        System.out.println("Starting SYOS Application...");
        
        try {
            ConfigurableApplicationContext context = SpringApplication.run(SyosApplication.class, args);
            
            // Test database connection
            testDatabaseConnection(context);
            
            System.out.println("SYOS Application started successfully!");
            
        } catch (Exception e) {
            System.err.println("Failed to start SYOS Application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test database connection using Spring context
     */
    private static void testDatabaseConnection(ConfigurableApplicationContext context) {
        try {
            System.out.println("\n=== Testing Database Connection ===");
            
            ConnectionPool connectionPool = context.getBean(ConnectionPool.class);
            
            // Test basic connectivity
            java.sql.Connection connection = connectionPool.getConnection();
            
            if (connection != null && connection.isValid(5)) {
                System.out.println("✓ Database connection successful!");
                
                // Get connection pool stats
                ConnectionPoolStats stats = connectionPool.getStats();
                System.out.println("Connection Pool Stats: " + stats);
                
                connection.close();
            } else {
                System.out.println("✗ Database connection failed!");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Database connection test failed: " + e.getMessage());
            System.out.println("Please ensure:");
            System.out.println("1. XAMPP is running");
            System.out.println("2. MySQL service is started");
            System.out.println("3. Database 'syos_db' exists");
            System.out.println("4. User 'syos_user' exists with correct password");
        }
    }
}
