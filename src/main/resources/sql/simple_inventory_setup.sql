-- Simple Inventory Tables Creation Script
-- Execute each section separately if needed

USE syos_db;

-- Create inventory_locations table
CREATE TABLE IF NOT EXISTS inventory_locations (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    shelf_qty INT DEFAULT 0,
    shelf_capacity INT DEFAULT 50,
    warehouse_qty INT DEFAULT 0,
    online_qty INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_product_inventory (product_id)
);

-- Create stock_movements table
CREATE TABLE IF NOT EXISTS stock_movements (
    movement_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    movement_type ENUM('SHELF_TO_WAREHOUSE', 'WAREHOUSE_TO_SHELF', 'PURCHASE_TO_SHELF', 'PURCHASE_TO_WAREHOUSE', 'SALE_FROM_SHELF', 'SALE_FROM_WAREHOUSE', 'ONLINE_SALE', 'STOCK_ADJUSTMENT', 'EXPIRED_REMOVAL') NOT NULL,
    location_from VARCHAR(20),
    location_to VARCHAR(20),
    quantity INT NOT NULL,
    reference_id VARCHAR(50),
    notes TEXT,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) DEFAULT 'System Administrator',
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create expiry_tracking table
CREATE TABLE IF NOT EXISTS expiry_tracking (
    batch_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    expiry_date DATE NOT NULL,
    quantity INT NOT NULL,
    location ENUM('SHELF', 'WAREHOUSE', 'ONLINE') NOT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'CLEARANCE', 'REMOVED') DEFAULT 'ACTIVE',
    purchase_date DATE,
    cost_price DECIMAL(10,2),
    notes TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create bill_counter table
CREATE TABLE IF NOT EXISTS bill_counter (
    counter_id INT PRIMARY KEY DEFAULT 1,
    last_bill_number INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Initialize bill counter
INSERT IGNORE INTO bill_counter (counter_id, last_bill_number) VALUES (1, 0);

-- Initialize inventory for existing products
INSERT IGNORE INTO inventory_locations (product_id, shelf_qty, shelf_capacity, warehouse_qty, online_qty)
SELECT 
    id as product_id,
    50 as shelf_qty,
    100 as shelf_capacity,
    100 as warehouse_qty,
    25 as online_qty
FROM products
WHERE id NOT IN (SELECT product_id FROM inventory_locations);