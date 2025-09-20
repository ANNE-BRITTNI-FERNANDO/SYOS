-- COMPREHENSIVE INVENTORY MANAGEMENT SYSTEM
-- This script creates the complete inventory structure for SYOS

USE syos_db;

-- 1. INVENTORY LOCATIONS TABLE
-- Stores shelf, warehouse, and online quantities for each product
CREATE TABLE IF NOT EXISTS inventory_locations (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    shelf_qty INT DEFAULT 0,
    shelf_capacity INT DEFAULT 50,  -- How many units can fit on shelf
    warehouse_qty INT DEFAULT 0,
    online_qty INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY unique_product_inventory (product_id)
);

-- 2. STOCK MOVEMENTS TABLE
-- Tracks all stock movements between locations
CREATE TABLE IF NOT EXISTS stock_movements (
    movement_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    movement_type ENUM('SHELF_TO_WAREHOUSE', 'WAREHOUSE_TO_SHELF', 'PURCHASE_TO_SHELF', 'PURCHASE_TO_WAREHOUSE', 'SALE_FROM_SHELF', 'SALE_FROM_WAREHOUSE', 'ONLINE_SALE', 'STOCK_ADJUSTMENT', 'EXPIRED_REMOVAL') NOT NULL,
    location_from VARCHAR(20),
    location_to VARCHAR(20),
    quantity INT NOT NULL,
    reference_id VARCHAR(50), -- Bill number, purchase order, etc.
    notes TEXT,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) DEFAULT 'System Administrator',
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_product_date (product_id, movement_date),
    INDEX idx_movement_type (movement_type)
);

-- 3. EXPIRY TRACKING TABLE
-- Tracks product batches with expiry dates
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
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_expiry_date (expiry_date),
    INDEX idx_product_expiry (product_id, expiry_date)
);

-- 4. CUSTOMERS TABLE
-- Store customer information for POS
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(15) UNIQUE NOT NULL,
    customer_name VARCHAR(100),
    email VARCHAR(100),
    address TEXT,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_purchases DECIMAL(12,2) DEFAULT 0.00,
    last_purchase_date TIMESTAMP NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE'
);

-- 5. TRANSACTIONS TABLE
-- Store all sales transactions
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id INT,
    transaction_type ENUM('PHYSICAL_STORE', 'ONLINE') DEFAULT 'PHYSICAL_STORE',
    subtotal DECIMAL(10,2) NOT NULL,
    total_discount DECIMAL(10,2) DEFAULT 0.00,
    final_total DECIMAL(10,2) NOT NULL,
    cash_received DECIMAL(10,2),
    change_amount DECIMAL(10,2),
    payment_method ENUM('CASH', 'CARD', 'DIGITAL') DEFAULT 'CASH',
    cashier_name VARCHAR(100) DEFAULT 'System Administrator',
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('COMPLETED', 'CANCELLED', 'PENDING') DEFAULT 'COMPLETED',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    INDEX idx_bill_number (bill_number),
    INDEX idx_transaction_date (transaction_date)
);

-- 6. TRANSACTION ITEMS TABLE
-- Store individual items in each transaction
CREATE TABLE IF NOT EXISTS transaction_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount_percent DECIMAL(5,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    line_total DECIMAL(10,2) NOT NULL,
    shelf_qty_used INT DEFAULT 0,
    warehouse_qty_used INT DEFAULT 0,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- 7. BILL COUNTER TABLE
-- Track bill numbering sequence
CREATE TABLE IF NOT EXISTS bill_counter (
    counter_id INT PRIMARY KEY DEFAULT 1,
    last_bill_number INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Initialize bill counter
INSERT IGNORE INTO bill_counter (counter_id, last_bill_number) VALUES (1, 0);

-- 8. Create inventory records for existing products
-- This ensures every product has an inventory location record
INSERT IGNORE INTO inventory_locations (product_id, shelf_qty, shelf_capacity, warehouse_qty, online_qty)
SELECT 
    product_id,
    COALESCE(quantity, 0) as shelf_qty,  -- Move existing quantity to shelf
    50 as shelf_capacity,  -- Default shelf capacity
    0 as warehouse_qty,    -- Start with 0 in warehouse
    0 as online_qty        -- Start with 0 online
FROM products
WHERE product_id NOT IN (SELECT product_id FROM inventory_locations);

-- 9. Update products table - remove quantity column since we're using inventory_locations
-- ALTER TABLE products DROP COLUMN IF EXISTS quantity;

-- 10. Create views for easy inventory management
CREATE OR REPLACE VIEW inventory_summary AS
SELECT 
    p.product_id,
    p.product_code,
    p.product_name,
    p.brand,
    p.category_id,
    c.category_name,
    p.selling_price,
    p.reorder_level,
    il.shelf_qty,
    il.shelf_capacity,
    il.warehouse_qty,
    il.online_qty,
    (il.shelf_qty + il.warehouse_qty + il.online_qty) as total_qty,
    CASE 
        WHEN (il.shelf_qty + il.warehouse_qty + il.online_qty) <= p.reorder_level THEN 'LOW'
        WHEN (il.shelf_qty + il.warehouse_qty + il.online_qty) <= (p.reorder_level * 2) THEN 'MEDIUM'
        ELSE 'GOOD'
    END as stock_status,
    il.last_updated
FROM products p
LEFT JOIN inventory_locations il ON p.product_id = il.product_id
LEFT JOIN categories c ON p.category_id = c.category_id;

-- 11. Create view for low stock alerts
CREATE OR REPLACE VIEW low_stock_alerts AS
SELECT 
    product_id,
    product_code,
    product_name,
    brand,
    category_name,
    total_qty,
    reorder_level,
    stock_status,
    shelf_qty,
    warehouse_qty,
    online_qty,
    CASE 
        WHEN total_qty = 0 THEN 'OUT OF STOCK - URGENT'
        WHEN total_qty <= 5 THEN 'CRITICAL - ORDER NOW'
        WHEN total_qty <= reorder_level THEN 'LOW - REORDER SOON'
        ELSE 'NORMAL'
    END as alert_level
FROM inventory_summary
WHERE total_qty <= reorder_level
ORDER BY total_qty ASC, alert_level DESC;

-- 12. Sample data for testing (optional)
-- Add some sample inventory data
UPDATE inventory_locations SET 
    shelf_qty = 25, 
    shelf_capacity = 50, 
    warehouse_qty = 100, 
    online_qty = 50 
WHERE product_id = 1;

COMMIT;

-- Display setup completion
SELECT 'INVENTORY SYSTEM SETUP COMPLETED SUCCESSFULLY!' as Status;
SELECT COUNT(*) as 'Products with Inventory Records' FROM inventory_locations;
SELECT COUNT(*) as 'Low Stock Products' FROM low_stock_alerts;
