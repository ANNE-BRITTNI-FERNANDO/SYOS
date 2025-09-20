package com.syos.infrastructure.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wrapper for pooled database connections
 * Provides additional functionality and tracking for connections from the pool
 */
public class PooledConnection implements AutoCloseable {
    
    private final Connection connection;
    private final long creationTime;
    private boolean closed;
    
    public PooledConnection(Connection connection) {
        this.connection = connection;
        this.creationTime = System.currentTimeMillis();
        this.closed = false;
    }
    
    /**
     * Gets the underlying JDBC connection
     */
    public Connection getConnection() throws SQLException {
        if (closed) {
            throw new SQLException("Connection has been closed");
        }
        return connection;
    }
    
    /**
     * Returns the connection to the pool
     */
    @Override
    public void close() throws SQLException {
        if (!closed && connection != null) {
            connection.close();
            closed = true;
        }
    }
    
    /**
     * Checks if the connection is closed
     */
    public boolean isClosed() throws SQLException {
        return closed || (connection != null && connection.isClosed());
    }
    
    /**
     * Gets the age of this connection in milliseconds
     */
    public long getAge() {
        return System.currentTimeMillis() - creationTime;
    }
    
    /**
     * Gets the creation time of this connection
     */
    public long getCreationTime() {
        return creationTime;
    }
    
    /**
     * Validates the connection is still usable
     */
    public boolean isValid(int timeout) throws SQLException {
        if (closed || connection == null) {
            return false;
        }
        return connection.isValid(timeout);
    }
    
    @Override
    public String toString() {
        try {
            return String.format(
                "PooledConnection{closed=%s, age=%dms, valid=%s}",
                closed, getAge(), !closed && connection.isValid(1)
            );
        } catch (SQLException e) {
            return String.format("PooledConnection{closed=%s, age=%dms, error=%s}",
                closed, getAge(), e.getMessage());
        }
    }
}
