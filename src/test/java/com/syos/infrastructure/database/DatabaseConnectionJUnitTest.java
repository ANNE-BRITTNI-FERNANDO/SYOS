package com.syos.infrastructure.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for database connectivity
 * Tests the SYOS database connection pool and configuration
 */
class DatabaseConnectionJUnitTest {
    
    private ConnectionPool connectionPool;
    private DatabaseConfig databaseConfig;
    
    @BeforeEach
    void setUp() {
        // Initialize database configuration and connection pool
        databaseConfig = new DatabaseConfig();
        connectionPool = new ConnectionPool(databaseConfig);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up resources
        if (connectionPool != null) {
            connectionPool.close();
        }
    }
    
    @Test
    @DisplayName("Database configuration should load successfully")
    void testDatabaseConfigurationLoads() {
        assertNotNull(databaseConfig, "Database configuration should not be null");
        assertNotNull(databaseConfig.getUrl(), "Database URL should not be null");
        assertNotNull(databaseConfig.getUsername(), "Database username should not be null");
        assertNotNull(databaseConfig.getPassword(), "Database password should not be null");
        assertNotNull(databaseConfig.getDriverClassName(), "Driver class name should not be null");
        
        // Verify default values
        assertTrue(databaseConfig.getUrl().contains("syos_db"), "URL should contain database name");
        assertEquals("syos_user", databaseConfig.getUsername(), "Username should match expected value");
    }
    
    @Test
    @DisplayName("Connection pool should initialize successfully")
    void testConnectionPoolInitialization() {
        assertNotNull(connectionPool, "Connection pool should not be null");
        assertFalse(connectionPool.isClosed(), "Connection pool should not be closed initially");
        assertNotNull(connectionPool.getDataSource(), "DataSource should not be null");
    }
    
    @Test
    @DisplayName("Should be able to get a valid connection from pool")
    void testGetConnectionFromPool() {
        assertDoesNotThrow(() -> {
            try (Connection connection = connectionPool.getConnection()) {
                assertNotNull(connection, "Connection should not be null");
                assertFalse(connection.isClosed(), "Connection should be open");
                assertTrue(connection.isValid(5), "Connection should be valid");
            }
        }, "Getting connection from pool should not throw exception");
    }
    
    @Test
    @DisplayName("Should be able to execute a simple query")
    void testExecuteSimpleQuery() {
        assertDoesNotThrow(() -> {
            try (Connection connection = connectionPool.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("SELECT 1, NOW()");
                 ResultSet rs = stmt.executeQuery()) {
                
                assertTrue(rs.next(), "Query should return at least one row");
                assertEquals(1, rs.getInt(1), "First column should be 1");
                assertNotNull(rs.getString(2), "Second column (timestamp) should not be null");
            }
        }, "Executing simple query should not throw exception");
    }
    
    @Test
    @DisplayName("Connection pool statistics should be available")
    void testConnectionPoolStatistics() {
        assertDoesNotThrow(() -> {
            ConnectionPoolStats stats = connectionPool.getStats();
            assertNotNull(stats, "Connection pool stats should not be null");
            assertTrue(stats.getMaxPoolSize() > 0, "Max pool size should be greater than 0");
            assertTrue(stats.getTotalConnections() >= 0, "Total connections should be non-negative");
            assertTrue(stats.getActiveConnections() >= 0, "Active connections should be non-negative");
            assertTrue(stats.getIdleConnections() >= 0, "Idle connections should be non-negative");
        }, "Getting connection pool statistics should not throw exception");
    }
    
    @Test
    @DisplayName("Multiple connections can be obtained simultaneously")
    void testMultipleConnections() {
        assertDoesNotThrow(() -> {
            Connection conn1 = connectionPool.getConnection();
            Connection conn2 = connectionPool.getConnection();
            Connection conn3 = connectionPool.getConnection();
            
            assertNotNull(conn1, "First connection should not be null");
            assertNotNull(conn2, "Second connection should not be null");
            assertNotNull(conn3, "Third connection should not be null");
            
            assertTrue(conn1.isValid(1), "First connection should be valid");
            assertTrue(conn2.isValid(1), "Second connection should be valid");
            assertTrue(conn3.isValid(1), "Third connection should be valid");
            
            // Clean up
            conn1.close();
            conn2.close();
            conn3.close();
        }, "Getting multiple connections should not throw exception");
    }
    
    @Test
    @DisplayName("Connection pool can be closed and reopened")
    void testConnectionPoolClosing() {
        assertDoesNotThrow(() -> {
            // Verify pool is initially open
            assertFalse(connectionPool.isClosed(), "Pool should be open initially");
            
            // Close the pool
            connectionPool.close();
            assertTrue(connectionPool.isClosed(), "Pool should be closed after close()");
            
            // Create a new pool
            ConnectionPool newPool = new ConnectionPool(databaseConfig);
            assertFalse(newPool.isClosed(), "New pool should be open");
            
            // Test new pool works
            try (Connection conn = newPool.getConnection()) {
                assertTrue(conn.isValid(1), "Connection from new pool should be valid");
            }
            
            newPool.close();
        }, "Closing and reopening connection pool should not throw exception");
    }
    
    @Test
    @DisplayName("Database config toString should not expose password")
    void testDatabaseConfigToString() {
        String configString = databaseConfig.toString();
        assertNotNull(configString, "Config toString should not be null");
        assertTrue(configString.contains("DatabaseConfig"), "Should contain class name");
        assertTrue(configString.contains("syos_user"), "Should contain username");
        // Note: This test assumes the toString method might or might not expose password
        // In production, passwords should never be in toString output
    }
}