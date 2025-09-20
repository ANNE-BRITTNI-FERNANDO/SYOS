package com.syos;

import java.sql.Connection;

import com.syos.infrastructure.database.ConnectionPool;
import com.syos.infrastructure.database.ConnectionPoolStats;

/**
 * Main application class for SYOS
 * Pure Java implementation without Spring framework
 */
public class SyosApplication {

    private static ConnectionPool connectionPool;

    public static void main(String[] args) {
        System.out.println("Starting SYOS Application...");
        
        try {
            // Initialize connection pool
            connectionPool = new ConnectionPool();
            
            // Test database connection
            testDatabaseConnection();
            
            System.out.println("SYOS Application started successfully!");
            
            // Application main loop (placeholder)
            runApplication();
            
        } catch (Exception e) {
            System.err.println("Failed to start SYOS Application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up resources
            shutdown();
        }
    }
    
    /**
     * Test database connection
     */
    private static void testDatabaseConnection() {
        try {
            System.out.println("\n=== Testing Database Connection ===");
            
            Connection connection = connectionPool.getConnection();
            
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
    
    /**
     * Main application logic (placeholder)
     */
    private static void runApplication() {
        System.out.println("\n=== SYOS Application Running ===");
        System.out.println("Application is ready for development...");
        
        // TODO: Add your application logic here
        // For now, just demonstrate that the app is running
        
        System.out.println("Press Ctrl+C to stop the application");
        
        // Keep application running (in real app, this would be your main logic)
        try {
            Thread.sleep(5000); // Sleep for 5 seconds as demonstration
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Cleanup and shutdown
     */
    private static void shutdown() {
        System.out.println("\n=== Shutting down SYOS Application ===");
        
        if (connectionPool != null) {
            connectionPool.close();
            System.out.println("✓ Connection pool closed");
        }
        
        System.out.println("✓ SYOS Application shutdown complete");
    }
    
    /**
     * Get the connection pool instance (for use by other parts of the application)
     */
    public static ConnectionPool getConnectionPool() {
        return connectionPool;
    }
}
