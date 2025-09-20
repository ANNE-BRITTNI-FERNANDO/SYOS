package syos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class SimpleConnectionPool {
    private String url;
    private String username;
    private String password;
    
    public SimpleConnectionPool() {
        loadDatabaseConfig();
    }
    
    private void loadDatabaseConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties properties = new Properties();
            if (input != null) {
                properties.load(input);
                this.url = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/syos_db");
                this.username = properties.getProperty("db.username", "syos_user");
                this.password = properties.getProperty("db.password", "temp1234");
            } else {
                // Default values
                this.url = "jdbc:mysql://localhost:3306/syos_db";
                this.username = "syos_user";
                this.password = "temp1234";
            }
            
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration: " + e.getMessage(), e);
        }
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    
    public void close() {
        // No connection pool to close with basic JDBC
    }
}