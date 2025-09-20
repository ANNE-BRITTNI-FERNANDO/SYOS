package com.syos.infrastructure.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection pool implementation using HikariCP
 * Manages database connections for the SYOS application
 */
@Component
public class ConnectionPool {
    
    private final HikariDataSource dataSource;
    private final DatabaseConfig databaseConfig;
    
    @Autowired
    public ConnectionPool(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
        this.dataSource = createDataSource();
    }
    
    /**
     * Creates and configures the HikariCP data source
     */
    private HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        
        // Basic connection settings
        config.setJdbcUrl(databaseConfig.getUrl());
        config.setUsername(databaseConfig.getUsername());
        config.setPassword(databaseConfig.getPassword());
        config.setDriverClassName(databaseConfig.getDriverClassName());
        
        // Pool configuration
        config.setMinimumIdle(databaseConfig.getMinIdleConnections());
        config.setMaximumPoolSize(databaseConfig.getMaxPoolSize());
        config.setConnectionTimeout(databaseConfig.getConnectionTimeout());
        config.setIdleTimeout(databaseConfig.getIdleTimeout());
        config.setMaxLifetime(databaseConfig.getMaxLifetime());
        
        // Additional settings for reliability
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("SYOS-Connection-Pool");
        config.setLeakDetectionThreshold(60000); // 60 seconds
        
        return new HikariDataSource(config);
    }
    
    /**
     * Gets a connection from the pool
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    /**
     * Gets the underlying DataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Gets connection pool statistics
     */
    public ConnectionPoolStats getStats() {
        return new ConnectionPoolStats(
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariConfigMXBean().getMaximumPoolSize()
        );
    }
    
    /**
     * Closes the connection pool
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    /**
     * Checks if the connection pool is closed
     */
    public boolean isClosed() {
        return dataSource == null || dataSource.isClosed();
    }
}
