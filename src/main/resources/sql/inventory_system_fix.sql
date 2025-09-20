-- INVENTORY SYSTEM FIX - Compatible with existing database structure
-- This script creates the missing inventory tables using correct foreign key references

USE syos_db;

-- 1. INVENTORY LOCATIONS TABLE (Fixed to use 'id' instead of 'product_id')
-- Note: The existing products table uses 'id' as primary key, not 'product_id'
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

-- 2. STOCK MOVEMENTS TABLE (Fixed to use 'id' instead of 'product_id')
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
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product_date (product_id, movement_date),
    INDEX idx_movement_type (movement_type)
);

-- 3. EXPIRY TRACKING TABLE (Fixed to use 'id' instead of 'product_id')
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
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_expiry_date (expiry_date),
    INDEX idx_product_expiry (product_id, expiry_date)
);

-- Note: customers and transactions tables already exist in the main database
-- So we don't need to recreate them

-- 4. TRANSACTION ITEMS TABLE (if needed, check if it exists)
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
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 5. BILL COUNTER TABLE
CREATE TABLE IF NOT EXISTS bill_counter (
    counter_id INT PRIMARY KEY DEFAULT 1,
    last_bill_number INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Initialize bill counter
INSERT IGNORE INTO bill_counter (counter_id, last_bill_number) VALUES (1, 0);

-- 6. Create inventory records for existing products
-- This ensures every product has an inventory location record
INSERT IGNORE INTO inventory_locations (product_id, shelf_qty, shelf_capacity, warehouse_qty, online_qty)
SELECT 
    id as product_id,           -- Use 'id' from products table
    50 as shelf_qty,            -- Default shelf quantity  
    100 as shelf_capacity,      -- Default shelf capacity
    100 as warehouse_qty,       -- Default warehouse quantity
    25 as online_qty            -- Default online quantity
FROM products
WHERE id NOT IN (SELECT product_id FROM inventory_locations);

-- 7. Create views for easy inventory management (Fixed column references)
CREATE OR REPLACE VIEW inventory_summary AS
SELECT 
    p.id as product_id,                    -- Use 'id' from products
    p.product_code,
    p.product_name,
    p.brand,
    p.category_id,
    p.unit_price as selling_price,         -- Use 'unit_price' instead of 'selling_price'
    50 as reorder_level,                   -- Default reorder level since products table may not have this
    il.shelf_qty,
    il.shelf_capacity,
    il.warehouse_qty,
    il.online_qty,
    (il.shelf_qty + il.warehouse_qty + il.online_qty) as total_qty,
    CASE 
        WHEN (il.shelf_qty + il.warehouse_qty + il.online_qty) <= 20 THEN 'LOW'
        WHEN (il.shelf_qty + il.warehouse_qty + il.online_qty) <= 50 THEN 'MEDIUM'
        ELSE 'GOOD'
    END as stock_status,
    il.last_updated
FROM products p
LEFT JOIN inventory_locations il ON p.id = il.product_id;

-- 8. Create view for low stock alerts (Fixed)
CREATE OR REPLACE VIEW low_stock_alerts AS
SELECT 
    product_id,
    product_code,
    product_name,
    brand,
    selling_price,
    total_qty,
    stock_status,
    shelf_qty,
    warehouse_qty,
    online_qty,
    CASE 
        WHEN total_qty = 0 THEN 'OUT OF STOCK - URGENT'
        WHEN total_qty <= 5 THEN 'CRITICAL - ORDER NOW'
        WHEN total_qty <= 20 THEN 'LOW - REORDER SOON'
        ELSE 'NORMAL'
    END as alert_level
FROM inventory_summary
WHERE total_qty <= 20
ORDER BY total_qty ASC, alert_level DESC;

COMMIT;

-- Display setup completion
SELECT 'INVENTORY SYSTEM FIX COMPLETED SUCCESSFULLY!' as Status;
SELECT 'Tables created for inventory management system' as Message;
SELECT COUNT(*) as 'Products with Inventory Records' FROM inventory_locations;
SELECT COUNT(*) as 'Low Stock Products' FROM low_stock_alerts;

-- Show table status
SHOW TABLES LIKE '%inventory%';
SHOW TABLES LIKE '%stock%';
SHOW TABLES LIKE '%expiry%';