-- ================================================================
-- SYOS (Store Your Outstanding Stock) Database Schema
-- Version: 2.0 - Hierarchical Categories with Smart Defaults
-- Date: September 21, 2025
-- ================================================================
-- 
-- This schema supports:
-- - Hierarchical category system (main categories + subcategories)
-- - Product-specific shelf management with intelligent defaults
-- - Category-based shelf capacity defaults for subcategories
-- - Complete inventory management with FIFO stock tracking
-- - User authentication and role-based access control
-- ================================================================

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS syos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE syos_db;

-- ================================================================
-- 1. USER MANAGEMENT TABLES
-- ================================================================

-- Users table for authentication and RBAC
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'CASHIER', 'CUSTOMER') DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- ================================================================
-- 2. HIERARCHICAL CATEGORY SYSTEM
-- ================================================================

-- Categories table with hierarchical structure and smart defaults
-- Main categories: organizational containers (parent_category_id = NULL/0)
-- Subcategories: where products are stored (parent_category_id > 0)
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(20) UNIQUE NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_category_id INT NULL,                    -- NULL/0 = main category, >0 = subcategory
    default_shelf_capacity INT DEFAULT 10,          -- Default shelf capacity for products in this subcategory
    default_minimum_threshold INT DEFAULT 2,        -- Default minimum threshold for products in this subcategory
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_parent_category (parent_category_id),
    INDEX idx_category_code (category_code),
    INDEX idx_is_active (is_active)
);

-- ================================================================
-- 3. PRODUCT MANAGEMENT TABLES
-- ================================================================

-- Products table with category-based shelf management
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) UNIQUE NOT NULL,       -- Hierarchical format: PARENT-SUB-001
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id INT NOT NULL,                       -- Must reference a SUBCATEGORY (not main category)
    unit_price DECIMAL(10,2) NOT NULL,
    brand VARCHAR(100),
    unit_of_measure VARCHAR(20) DEFAULT 'pcs',
    reorder_level INT DEFAULT 50,
    shelf_display_capacity INT NOT NULL,            -- Product-specific shelf capacity
    shelf_minimum_threshold INT NOT NULL,           -- Product-specific minimum threshold for auto-restock
    auto_restock_enabled BOOLEAN DEFAULT TRUE,
    expiry_date DATE NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by_user_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_product_code (product_code),
    INDEX idx_category_id (category_id),
    INDEX idx_is_active (is_active),
    INDEX idx_expiry_date (expiry_date),
    -- Constraint to ensure products are only added to subcategories
    CONSTRAINT chk_subcategory_only CHECK (
        category_id IN (SELECT id FROM categories WHERE parent_category_id IS NOT NULL AND parent_category_id > 0)
    )
);

-- ================================================================
-- 4. INVENTORY MANAGEMENT TABLES
-- ================================================================

-- Stock batches for FIFO inventory management
CREATE TABLE IF NOT EXISTS stock_batches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    batch_code VARCHAR(50) NOT NULL,
    supplier_name VARCHAR(255),
    quantity_received INT NOT NULL,
    quantity_remaining INT NOT NULL,
    unit_cost DECIMAL(10,2),
    expiry_date DATE,
    received_date DATE NOT NULL,
    storage_location VARCHAR(100) DEFAULT 'Physical Store',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_expiry_date (expiry_date),
    INDEX idx_received_date (received_date),
    INDEX idx_quantity_remaining (quantity_remaining)
);

-- Stock movements for audit trail
CREATE TABLE IF NOT EXISTS stock_movements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    batch_id INT,
    movement_type ENUM('IN', 'OUT', 'ADJUSTMENT', 'EXPIRED', 'DAMAGED') NOT NULL,
    quantity INT NOT NULL,
    remaining_stock INT NOT NULL,
    reference_number VARCHAR(100),              -- Bill number, adjustment reference, etc.
    notes TEXT,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id INT,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES stock_batches(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_product_id (product_id),
    INDEX idx_movement_type (movement_type),
    INDEX idx_movement_date (movement_date)
);

-- ================================================================
-- 5. SALES AND POS TABLES
-- ================================================================

-- Sales transactions
CREATE TABLE IF NOT EXISTS sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    customer_phone VARCHAR(20),
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    final_amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'DIGITAL', 'CREDIT') DEFAULT 'CASH',
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cashier_id INT,
    notes TEXT,
    FOREIGN KEY (cashier_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_bill_number (bill_number),
    INDEX idx_sale_date (sale_date),
    INDEX idx_cashier_id (cashier_id)
);

-- Sale items (cart items)
CREATE TABLE IF NOT EXISTS sale_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    INDEX idx_sale_id (sale_id),
    INDEX idx_product_id (product_id)
);

-- ================================================================
-- 6. SYSTEM CONFIGURATION TABLES
-- ================================================================

-- System settings
CREATE TABLE IF NOT EXISTS system_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT,
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by_user_id INT,
    FOREIGN KEY (updated_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ================================================================
-- 7. INSERT SAMPLE DATA
-- ================================================================

-- Insert default admin user
INSERT IGNORE INTO users (email, password, first_name, last_name, role, is_active, email_verified) 
VALUES ('Admin@gmail.com', 'Admin1234*', 'System', 'Administrator', 'ADMIN', TRUE, TRUE);

-- Insert sample main categories (organizational containers)
INSERT IGNORE INTO categories (category_code, category_name, description, parent_category_id, default_shelf_capacity, default_minimum_threshold) VALUES
('CAT001', 'Electronics', 'Electronic devices and accessories', NULL, 0, 0),
('CAT002', 'Clothing', 'Apparel and fashion items', NULL, 0, 0),
('CAT003', 'Books', 'Books and educational materials', NULL, 0, 0),
('CAT004', 'Home & Garden', 'Home improvement and gardening items', NULL, 0, 0),
('CAT005', 'Sports', 'Sports equipment and accessories', NULL, 0, 0);

-- Insert sample subcategories (where products are stored) with smart defaults
INSERT IGNORE INTO categories (category_code, category_name, description, parent_category_id, default_shelf_capacity, default_minimum_threshold) VALUES
-- Electronics subcategories (moderate defaults)
('LAPTOP', 'Laptops & Computers', 'Portable and desktop computers', 1, 15, 3),
('MOBILE', 'Mobile Phones', 'Smartphones and accessories', 1, 15, 3),
('AUDIO', 'Audio & Headphones', 'Speakers, headphones, audio equipment', 1, 15, 3),

-- Clothing subcategories (high defaults - fast moving)
('MENS', 'Men\'s Clothing', 'Men\'s apparel and accessories', 2, 20, 5),
('WOMENS', 'Women\'s Clothing', 'Women\'s apparel and accessories', 2, 20, 5),
('KIDS', 'Kids Clothing', 'Children\'s apparel and accessories', 2, 20, 5),

-- Books subcategories (conservative defaults)
('FICTION', 'Fiction Books', 'Novels, stories, literature', 3, 5, 1),
('TECH', 'Technical Books', 'Programming, engineering, technical manuals', 3, 5, 1),
('EDUC', 'Educational Books', 'Textbooks, study materials', 3, 5, 1),

-- Sports subcategories (moderate defaults)
('CRICKET', 'Cricket Equipment', 'Cricket bats, balls, protective gear', 5, 8, 2),
('FITNESS', 'Fitness Equipment', 'Gym equipment, weights, accessories', 5, 8, 2);

-- Insert system settings
INSERT IGNORE INTO system_settings (setting_key, setting_value, description) VALUES
('company_name', 'SYOS Store', 'Company name for receipts and reports'),
('tax_rate', '15.0', 'Default tax rate percentage'),
('currency', 'LKR', 'Default currency code'),
('auto_restock_enabled', 'true', 'Enable automatic restocking when below threshold'),
('fifo_enabled', 'true', 'Enable First-In-First-Out inventory management');

-- ================================================================
-- 8. USEFUL VIEWS FOR REPORTING
-- ================================================================

-- View for hierarchical category display
CREATE OR REPLACE VIEW category_hierarchy AS
SELECT 
    c.id,
    c.category_code,
    c.category_name,
    c.description,
    c.parent_category_id,
    p.category_name as parent_name,
    c.default_shelf_capacity,
    c.default_minimum_threshold,
    c.is_active,
    CASE 
        WHEN c.parent_category_id IS NULL OR c.parent_category_id = 0 THEN 'Main Category'
        ELSE 'Subcategory'
    END as category_type
FROM categories c
LEFT JOIN categories p ON c.parent_category_id = p.id
WHERE c.is_active = TRUE
ORDER BY p.category_name, c.category_name;

-- View for product inventory with category information
CREATE OR REPLACE VIEW product_inventory AS
SELECT 
    p.id,
    p.product_code,
    p.product_name,
    c.category_name as subcategory_name,
    pc.category_name as main_category_name,
    p.unit_price,
    p.shelf_display_capacity,
    p.shelf_minimum_threshold,
    COALESCE(SUM(sb.quantity_remaining), 0) as current_stock,
    p.reorder_level,
    p.auto_restock_enabled,
    p.is_active,
    CASE 
        WHEN COALESCE(SUM(sb.quantity_remaining), 0) <= p.shelf_minimum_threshold THEN 'LOW STOCK'
        WHEN COALESCE(SUM(sb.quantity_remaining), 0) >= p.shelf_display_capacity THEN 'FULL'
        ELSE 'NORMAL'
    END as stock_status
FROM products p
JOIN categories c ON p.category_id = c.id
LEFT JOIN categories pc ON c.parent_category_id = pc.id
LEFT JOIN stock_batches sb ON p.id = sb.product_id
WHERE p.is_active = TRUE
GROUP BY p.id, p.product_code, p.product_name, c.category_name, pc.category_name, 
         p.unit_price, p.shelf_display_capacity, p.shelf_minimum_threshold, 
         p.reorder_level, p.auto_restock_enabled, p.is_active;

-- ================================================================
-- 9. STORED PROCEDURES FOR COMMON OPERATIONS
-- ================================================================

DELIMITER //

-- Procedure to add stock and create movement record
CREATE PROCEDURE IF NOT EXISTS AddStock(
    IN p_product_id INT,
    IN p_batch_code VARCHAR(50),
    IN p_supplier_name VARCHAR(255),
    IN p_quantity INT,
    IN p_unit_cost DECIMAL(10,2),
    IN p_expiry_date DATE,
    IN p_storage_location VARCHAR(100),
    IN p_user_id INT
)
BEGIN
    DECLARE batch_id INT;
    
    START TRANSACTION;
    
    -- Insert stock batch
    INSERT INTO stock_batches (product_id, batch_code, supplier_name, quantity_received, 
                              quantity_remaining, unit_cost, expiry_date, received_date, storage_location)
    VALUES (p_product_id, p_batch_code, p_supplier_name, p_quantity, p_quantity, 
            p_unit_cost, p_expiry_date, CURDATE(), p_storage_location);
    
    SET batch_id = LAST_INSERT_ID();
    
    -- Record stock movement
    INSERT INTO stock_movements (product_id, batch_id, movement_type, quantity, 
                                remaining_stock, reference_number, created_by_user_id)
    VALUES (p_product_id, batch_id, 'IN', p_quantity,
            (SELECT SUM(quantity_remaining) FROM stock_batches WHERE product_id = p_product_id),
            p_batch_code, p_user_id);
    
    COMMIT;
END //

DELIMITER ;

-- ================================================================
-- 10. INDEXES FOR PERFORMANCE
-- ================================================================

-- Additional performance indexes
CREATE INDEX IF NOT EXISTS idx_products_shelf_status ON products(shelf_minimum_threshold, shelf_display_capacity);
CREATE INDEX IF NOT EXISTS idx_stock_batches_fifo ON stock_batches(product_id, received_date, quantity_remaining);
CREATE INDEX IF NOT EXISTS idx_sales_reporting ON sales(sale_date, cashier_id, total_amount);

-- ================================================================
-- SCHEMA COMPLETE
-- ================================================================
-- 
-- This schema supports:
-- ✅ Hierarchical categories (main + subcategories)
-- ✅ Product-specific shelf management
-- ✅ Category-based intelligent defaults
-- ✅ FIFO inventory tracking
-- ✅ Complete POS system
-- ✅ User authentication & RBAC
-- ✅ Audit trails and reporting
-- ✅ Performance optimizations
-- 
-- Usage Notes:
-- - Main categories: organizational only, no shelf defaults
-- - Subcategories: where products are stored, have shelf defaults
-- - Products: can only be added to subcategories, inherit defaults
-- - Inventory: FIFO-based with automatic stock movements
-- ================================================================