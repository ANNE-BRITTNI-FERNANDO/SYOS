package com.syos.infrastructure.database;

/**
 * Connection pool statistics holder
 * Provides information about the current state of the connection pool
 */
public class ConnectionPoolStats {
    
    private final int activeConnections;
    private final int idleConnections;
    private final int totalConnections;
    private final int maxPoolSize;
    
    public ConnectionPoolStats(int activeConnections, int idleConnections, 
                              int totalConnections, int maxPoolSize) {
        this.activeConnections = activeConnections;
        this.idleConnections = idleConnections;
        this.totalConnections = totalConnections;
        this.maxPoolSize = maxPoolSize;
    }
    
    public int getActiveConnections() {
        return activeConnections;
    }
    
    public int getIdleConnections() {
        return idleConnections;
    }
    
    public int getTotalConnections() {
        return totalConnections;
    }
    
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    public double getPoolUtilization() {
        return maxPoolSize > 0 ? (double) totalConnections / maxPoolSize * 100 : 0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "ConnectionPoolStats{active=%d, idle=%d, total=%d, max=%d, utilization=%.1f%%}",
            activeConnections, idleConnections, totalConnections, maxPoolSize, getPoolUtilization()
        );
    }
}
