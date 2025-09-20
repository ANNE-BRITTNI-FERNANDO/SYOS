package com.syos.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SimpleInventoryCreator {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/syos_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "syos_user";
    private static final String PASSWORD = "temp1234";
    
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("SYOS Simple Inventory Creator");
        System.out.println("=================================");
        
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to database
            System.out.println("Connecting to database...");
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("✓ Connected to database successfully");
            
            Statement stmt = connection.createStatement();
            
            // Execute USE statement first
            System.out.println("1. Selecting database...");
            stmt.execute("USE syos_db");
            System.out.println("✓ Database selected");
            
            // Create inventory_locations table
            System.out.println("2. Creating inventory_locations table...");
            String createInventoryLocations = "CREATE TABLE IF NOT EXISTS inventory_locations (" +
                "inventory_id INT AUTO_INCREMENT PRIMARY KEY," +
                "product_id INT NOT NULL," +
                "shelf_qty INT DEFAULT 0," +
                "shelf_capacity INT DEFAULT 50," +
                "warehouse_qty INT DEFAULT 0," +
                "online_qty INT DEFAULT 0," +
                "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE," +
                "UNIQUE KEY unique_product_inventory (product_id)" +
                ")";
            stmt.execute(createInventoryLocations);
            System.out.println("✓ inventory_locations table created");
            
            // Create stock_movements table
            System.out.println("3. Creating stock_movements table...");
            String createStockMovements = "CREATE TABLE IF NOT EXISTS stock_movements (" +
                "movement_id INT AUTO_INCREMENT PRIMARY KEY," +
                "product_id INT NOT NULL," +
                "movement_type ENUM('SHELF_TO_WAREHOUSE', 'WAREHOUSE_TO_SHELF', 'PURCHASE_TO_SHELF', 'PURCHASE_TO_WAREHOUSE', 'SALE_FROM_SHELF', 'SALE_FROM_WAREHOUSE', 'ONLINE_SALE', 'STOCK_ADJUSTMENT', 'EXPIRED_REMOVAL') NOT NULL," +
                "location_from VARCHAR(20)," +
                "location_to VARCHAR(20)," +
                "quantity INT NOT NULL," +
                "reference_id VARCHAR(50)," +
                "notes TEXT," +
                "movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "created_by VARCHAR(100) DEFAULT 'System Administrator'," +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE" +
                ")";
            stmt.execute(createStockMovements);
            System.out.println("✓ stock_movements table created");
            
            // Create expiry_tracking table
            System.out.println("4. Creating expiry_tracking table...");
            String createExpiryTracking = "CREATE TABLE IF NOT EXISTS expiry_tracking (" +
                "batch_id INT AUTO_INCREMENT PRIMARY KEY," +
                "product_id INT NOT NULL," +
                "batch_number VARCHAR(50) NOT NULL," +
                "expiry_date DATE NOT NULL," +
                "quantity INT NOT NULL," +
                "location ENUM('SHELF', 'WAREHOUSE', 'ONLINE') NOT NULL," +
                "status ENUM('ACTIVE', 'EXPIRED', 'CLEARANCE', 'REMOVED') DEFAULT 'ACTIVE'," +
                "purchase_date DATE," +
                "cost_price DECIMAL(10,2)," +
                "notes TEXT," +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE" +
                ")";
            stmt.execute(createExpiryTracking);
            System.out.println("✓ expiry_tracking table created");
            
            // Create bill_counter table
            System.out.println("5. Creating bill_counter table...");
            String createBillCounter = "CREATE TABLE IF NOT EXISTS bill_counter (" +
                "counter_id INT PRIMARY KEY DEFAULT 1," +
                "last_bill_number INT DEFAULT 0," +
                "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";
            stmt.execute(createBillCounter);
            System.out.println("✓ bill_counter table created");
            
            // Initialize bill counter
            System.out.println("6. Initializing bill counter...");
            stmt.execute("INSERT IGNORE INTO bill_counter (counter_id, last_bill_number) VALUES (1, 0)");
            System.out.println("✓ Bill counter initialized");
            
            // Initialize inventory for existing products
            System.out.println("7. Initializing inventory for existing products...");
            String initInventory = "INSERT IGNORE INTO inventory_locations (product_id, shelf_qty, shelf_capacity, warehouse_qty, online_qty) " +
                "SELECT id as product_id, 50 as shelf_qty, 100 as shelf_capacity, 100 as warehouse_qty, 25 as online_qty " +
                "FROM products WHERE id NOT IN (SELECT product_id FROM inventory_locations)";
            int rowsInserted = stmt.executeUpdate(initInventory);
            System.out.println("✓ " + rowsInserted + " products initialized with inventory data");
            
            // Verify tables created
            System.out.println("8. Verifying tables...");
            var rs = stmt.executeQuery("SHOW TABLES LIKE '%inventory%'");
            while (rs.next()) {
                System.out.println("  ✓ " + rs.getString(1));
            }
            rs.close();
            
            rs = stmt.executeQuery("SHOW TABLES LIKE '%stock%'");
            while (rs.next()) {
                System.out.println("  ✓ " + rs.getString(1));
            }
            rs.close();
            
            rs = stmt.executeQuery("SHOW TABLES LIKE '%expiry%'");
            while (rs.next()) {
                System.out.println("  ✓ " + rs.getString(1));
            }
            rs.close();
            
            rs = stmt.executeQuery("SHOW TABLES LIKE '%bill%'");
            while (rs.next()) {
                System.out.println("  ✓ " + rs.getString(1));
            }
            rs.close();
            
            // Count inventory records
            rs = stmt.executeQuery("SELECT COUNT(*) FROM inventory_locations");
            if (rs.next()) {
                System.out.println("  ✓ " + rs.getInt(1) + " products have inventory records");
            }
            rs.close();
            
            stmt.close();
            connection.close();
            
            System.out.println("\n=================================");
            System.out.println("✓ ALL INVENTORY TABLES CREATED SUCCESSFULLY!");
            System.out.println("Your POS system is now ready to use!");
            System.out.println("=================================");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found!");
            System.err.println("Make sure mysql-connector-j-9.4.0.jar is in the lib folder");
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}