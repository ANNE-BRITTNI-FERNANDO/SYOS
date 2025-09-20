package com.syos.infrastructure.database;

/**
 * Configuration class for connection pool settings
 * Pure Java implementation without Spring framework
 */
public class ConnectionPoolConfig {
    
    private int initialSize = 5;
    private int maxSize = 20;
    private int minIdle = 5;
    private long connectionTimeout = 30000;
    private long idleTimeout = 600000;
    private long maxLifetime = 1800000;
    private String validationQuery = "SELECT 1";
    private boolean testOnBorrow = true;
    private boolean testWhileIdle = true;
    private long validationInterval = 30000;
    
    // Getters and Setters
    public int getInitialSize() {
        return initialSize;
    }
    
    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public int getMinIdle() {
        return minIdle;
    }
    
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }
    
    public long getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public long getIdleTimeout() {
        return idleTimeout;
    }
    
    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }
    
    public long getMaxLifetime() {
        return maxLifetime;
    }
    
    public void setMaxLifetime(long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }
    
    public String getValidationQuery() {
        return validationQuery;
    }
    
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }
    
    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }
    
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }
    
    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }
    
    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }
    
    public long getValidationInterval() {
        return validationInterval;
    }
    
    public void setValidationInterval(long validationInterval) {
        this.validationInterval = validationInterval;
    }
    
    @Override
    public String toString() {
        return "ConnectionPoolConfig{" +
                "initialSize=" + initialSize +
                ", maxSize=" + maxSize +
                ", minIdle=" + minIdle +
                ", connectionTimeout=" + connectionTimeout +
                ", idleTimeout=" + idleTimeout +
                ", maxLifetime=" + maxLifetime +
                ", validationQuery='" + validationQuery + '\'' +
                ", testOnBorrow=" + testOnBorrow +
                ", testWhileIdle=" + testWhileIdle +
                ", validationInterval=" + validationInterval +
                '}';
    }
}
