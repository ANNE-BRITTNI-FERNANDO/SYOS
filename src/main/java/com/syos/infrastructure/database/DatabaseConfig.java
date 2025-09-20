package com.syos.infrastructure.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Database configuration class for SYOS application
 * Contains database connection parameters for XAMPP MySQL
 * Uses pure Java with properties file loading
 */
public class DatabaseConfig {
    
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private int initialPoolSize;
    private int maxPoolSize;
    private int minIdleConnections;
    private long connectionTimeout;
    private long idleTimeout;
    private long maxLifetime;

    public DatabaseConfig() {
        loadConfiguration();
    }

    /**
     * Load configuration from properties file
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load config.properties, using default values");
        }
        
        // Load properties with default values
        this.url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/syos_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        this.username = props.getProperty("db.username", "syos_user");
        this.password = props.getProperty("db.password", "temp1234");
        this.driverClassName = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        this.initialPoolSize = Integer.parseInt(props.getProperty("db.pool.initial.size", "5"));
        this.maxPoolSize = Integer.parseInt(props.getProperty("db.pool.max.size", "20"));
        this.minIdleConnections = Integer.parseInt(props.getProperty("db.pool.min.idle", "5"));
        this.connectionTimeout = Long.parseLong(props.getProperty("db.connection.timeout", "30000"));
        this.idleTimeout = Long.parseLong(props.getProperty("db.idle.timeout", "600000"));
        this.maxLifetime = Long.parseLong(props.getProperty("db.max.lifetime", "1800000"));
    }

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
