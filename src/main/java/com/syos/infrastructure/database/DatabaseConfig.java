package com.syos.infrastructure.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Database configuration class for SYOS application
 * Contains database connection parameters for XAMPP MySQL
 */
@Configuration
@PropertySource("classpath:config.properties")
public class DatabaseConfig {
    
    @Value("${db.url:jdbc:mysql://localhost:3306/syos_db}")
    private String url;
    
    @Value("${db.username:syos_user}")
    private String username;
    
    @Value("${db.password:temp1234}")
    private String password;
    
    @Value("${db.driver:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;
    
    @Value("${db.pool.initial.size:5}")
    private int initialPoolSize;
    
    @Value("${db.pool.max.size:20}")
    private int maxPoolSize;
    
    @Value("${db.pool.min.idle:5}")
    private int minIdleConnections;
    
    @Value("${db.connection.timeout:30000}")
    private long connectionTimeout;
    
    @Value("${db.idle.timeout:600000}")
    private long idleTimeout;
    
    @Value("${db.max.lifetime:1800000}")
    private long maxLifetime;

    // Getters
    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getMinIdleConnections() {
        return minIdleConnections;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public long getMaxLifetime() {
        return maxLifetime;
    }

    @Override
    public String toString() {
        return "DatabaseConfig{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", initialPoolSize=" + initialPoolSize +
                ", maxPoolSize=" + maxPoolSize +
                ", minIdleConnections=" + minIdleConnections +
                ", connectionTimeout=" + connectionTimeout +
                ", idleTimeout=" + idleTimeout +
                ", maxLifetime=" + maxLifetime +
                '}';
    }
}
