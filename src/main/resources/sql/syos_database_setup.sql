-- SYOS (Synex Outlet Store) Management System Database Schema
-- Complete database setup for retail management system

-- Create the database
CREATE DATABASE IF NOT EXISTS syos_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE syos_db;

-- Create the database user (run this as root)
CREATE USER IF NOT EXISTS 'syos_user'@'localhost' IDENTIFIED BY 'temp1234';
GRANT ALL PRIVILEGES ON syos_db.* TO 'syos_user'@'localhost';
CREATE USER IF NOT EXISTS 'syos_user'@'%' IDENTIFIED BY 'temp1234';
GRANT ALL PRIVILEGES ON syos_db.* TO 'syos_user'@'%';
FLUSH PRIVILEGES;

-- ================================
-- CORE SYSTEM TABLES
-- ================================

-- Roles and Permissions System
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    permission_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    module VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role_permissions (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Users System
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_code VARCHAR(20) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ================================
-- PRODUCT MANAGEMENT TABLES
-- ================================

-- Product Categories
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(20) UNIQUE NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_category_id INT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES categories(id)
);

-- Products
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) UNIQUE NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    selling_price DECIMAL(10,2) NOT NULL,
    brand VARCHAR(100),
    model VARCHAR(100),
    color VARCHAR(50),
    size VARCHAR(50),
    weight DECIMAL(8,3),
    dimensions VARCHAR(100),
    warranty_period INT DEFAULT 0, -- in months
    minimum_stock_level INT DEFAULT 0,
    maximum_stock_level INT DEFAULT 1000,
    reorder_level INT DEFAULT 10,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Storage Locations
CREATE TABLE locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    location_code VARCHAR(20) UNIQUE NOT NULL,
    location_name VARCHAR(100) NOT NULL,
    location_type ENUM('WAREHOUSE', 'STORE', 'SECTION', 'SHELF') NOT NULL,
    parent_location_id INT NULL,
    capacity INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_location_id) REFERENCES locations(id)
);

-- Product Batches (for tracking stock lots)
CREATE TABLE batches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    batch_code VARCHAR(50) UNIQUE NOT NULL,
    product_id INT NOT NULL,
    supplier_name VARCHAR(100),
    purchase_price DECIMAL(10,2) NOT NULL,
    quantity_received INT NOT NULL,
    quantity_remaining INT NOT NULL,
    manufacturing_date DATE,
    expiry_date DATE,
    received_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    location_id INT,
    notes TEXT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

-- Inventory Management
CREATE TABLE inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    location_id INT NOT NULL,
    batch_id INT,
    quantity_on_hand INT NOT NULL DEFAULT 0,
    quantity_reserved INT NOT NULL DEFAULT 0,
    quantity_available INT GENERATED ALWAYS AS (quantity_on_hand - quantity_reserved) STORED,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_product_location_batch (product_id, location_id, batch_id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (location_id) REFERENCES locations(id),
    FOREIGN KEY (batch_id) REFERENCES batches(id)
);

-- ================================
-- CUSTOMER MANAGEMENT TABLES
-- ================================

-- Customers
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address_line1 VARCHAR(200),
    address_line2 VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Sri Lanka',
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    loyalty_points INT DEFAULT 0,
    total_purchases DECIMAL(12,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ================================
-- TRANSACTION TABLES
-- ================================

-- Transactions (POS Sales)
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_code VARCHAR(20) UNIQUE NOT NULL,
    customer_id INT NULL,
    user_id INT NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'BANK_TRANSFER', 'DIGITAL_WALLET') NOT NULL,
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'COMPLETED',
    notes TEXT,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Transaction Items
CREATE TABLE transaction_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    product_id INT NOT NULL,
    batch_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    line_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (batch_id) REFERENCES batches(id)
);

-- ================================
-- ORDER MANAGEMENT TABLES
-- ================================

-- Orders (for future deliveries, pre-orders, etc.)
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(20) UNIQUE NOT NULL,
    customer_id INT NOT NULL,
    user_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    required_date DATE,
    delivery_date DATE NULL,
    order_status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    shipping_cost DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('PENDING', 'PARTIAL', 'PAID', 'REFUNDED') DEFAULT 'PENDING',
    delivery_address TEXT,
    notes TEXT,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order Items
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity_ordered INT NOT NULL,
    quantity_delivered INT DEFAULT 0,
    unit_price DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    line_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ================================
-- SYSTEM AUDIT TABLES
-- ================================

-- Inventory Movements (for tracking stock changes)
CREATE TABLE inventory_movements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    batch_id INT,
    location_id INT,
    movement_type ENUM('IN', 'OUT', 'TRANSFER', 'ADJUSTMENT') NOT NULL,
    quantity INT NOT NULL,
    reference_type ENUM('TRANSACTION', 'ORDER', 'PURCHASE', 'ADJUSTMENT', 'TRANSFER') NOT NULL,
    reference_id INT,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL,
    notes TEXT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (batch_id) REFERENCES batches(id),
    FOREIGN KEY (location_id) REFERENCES locations(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- User Sessions (for tracking logins)
CREATE TABLE user_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    session_token VARCHAR(255) UNIQUE,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- System Logs
CREATE TABLE system_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    log_level ENUM('INFO', 'WARNING', 'ERROR', 'DEBUG') NOT NULL,
    module VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    description TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ================================
-- INDEXES FOR PERFORMANCE
-- ================================

-- User indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_user_code ON users(user_code);
CREATE INDEX idx_users_role ON users(role_id);

-- Product indexes
CREATE INDEX idx_products_code ON products(product_code);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_name ON products(product_name);
CREATE INDEX idx_products_active ON products(is_active);

-- Inventory indexes
CREATE INDEX idx_inventory_product ON inventory(product_id);
CREATE INDEX idx_inventory_location ON inventory(location_id);
CREATE INDEX idx_inventory_batch ON inventory(batch_id);

-- Transaction indexes
CREATE INDEX idx_transactions_code ON transactions(transaction_code);
CREATE INDEX idx_transactions_customer ON transactions(customer_id);
CREATE INDEX idx_transactions_user ON transactions(user_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);

-- Customer indexes
CREATE INDEX idx_customers_code ON customers(customer_code);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_phone ON customers(phone);

-- ================================
-- INITIAL DATA SETUP
-- ================================

-- Insert default roles
INSERT INTO roles (role_name, description) VALUES 
('ADMIN', 'System Administrator with full access'),
('MANAGER', 'Store Manager with management privileges'),
('CASHIER', 'Point of Sale operator'),
('INVENTORY_CLERK', 'Inventory management staff'),
('SALES_ASSOCIATE', 'Sales floor staff');

-- Insert default permissions
INSERT INTO permissions (permission_name, description, module) VALUES 
-- User Management
('USER_CREATE', 'Create new users', 'USER_MANAGEMENT'),
('USER_READ', 'View user information', 'USER_MANAGEMENT'),
('USER_UPDATE', 'Update user information', 'USER_MANAGEMENT'),
('USER_DELETE', 'Delete users', 'USER_MANAGEMENT'),

-- Product Management
('PRODUCT_CREATE', 'Create new products', 'PRODUCT_MANAGEMENT'),
('PRODUCT_READ', 'View product information', 'PRODUCT_MANAGEMENT'),
('PRODUCT_UPDATE', 'Update product information', 'PRODUCT_MANAGEMENT'),
('PRODUCT_DELETE', 'Delete products', 'PRODUCT_MANAGEMENT'),

-- Inventory Management
('INVENTORY_READ', 'View inventory levels', 'INVENTORY_MANAGEMENT'),
('INVENTORY_UPDATE', 'Update inventory levels', 'INVENTORY_MANAGEMENT'),
('INVENTORY_TRANSFER', 'Transfer inventory between locations', 'INVENTORY_MANAGEMENT'),

-- Sales/POS
('POS_SALE', 'Process sales transactions', 'POINT_OF_SALE'),
('POS_REFUND', 'Process refunds', 'POINT_OF_SALE'),
('POS_DISCOUNT', 'Apply discounts', 'POINT_OF_SALE'),

-- Customer Management
('CUSTOMER_CREATE', 'Create new customers', 'CUSTOMER_MANAGEMENT'),
('CUSTOMER_READ', 'View customer information', 'CUSTOMER_MANAGEMENT'),
('CUSTOMER_UPDATE', 'Update customer information', 'CUSTOMER_MANAGEMENT'),

-- Orders
('ORDER_CREATE', 'Create new orders', 'ORDER_MANAGEMENT'),
('ORDER_READ', 'View orders', 'ORDER_MANAGEMENT'),
('ORDER_UPDATE', 'Update order status', 'ORDER_MANAGEMENT'),
('ORDER_CANCEL', 'Cancel orders', 'ORDER_MANAGEMENT'),

-- Reports
('REPORT_SALES', 'View sales reports', 'REPORTING'),
('REPORT_INVENTORY', 'View inventory reports', 'REPORTING'),
('REPORT_CUSTOMER', 'View customer reports', 'REPORTING'),
('REPORT_FINANCIAL', 'View financial reports', 'REPORTING');

-- Assign permissions to roles
-- ADMIN - All permissions
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 1, id FROM permissions;

-- MANAGER - Most permissions except user delete
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 2, id FROM permissions WHERE permission_name != 'USER_DELETE';

-- CASHIER - POS and basic customer operations
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 3, id FROM permissions WHERE permission_name IN (
    'PRODUCT_READ', 'CUSTOMER_CREATE', 'CUSTOMER_READ', 'CUSTOMER_UPDATE',
    'POS_SALE', 'POS_REFUND', 'POS_DISCOUNT', 'INVENTORY_READ'
);

-- INVENTORY_CLERK - Inventory and product operations
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 4, id FROM permissions WHERE permission_name LIKE 'INVENTORY_%' 
    OR permission_name LIKE 'PRODUCT_%' OR permission_name = 'REPORT_INVENTORY';

-- SALES_ASSOCIATE - Sales and customer operations
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 5, id FROM permissions WHERE permission_name IN (
    'PRODUCT_READ', 'CUSTOMER_CREATE', 'CUSTOMER_READ', 'CUSTOMER_UPDATE',
    'ORDER_CREATE', 'ORDER_READ', 'ORDER_UPDATE', 'INVENTORY_READ'
);

-- Create default admin user (password: admin123)
INSERT INTO users (user_code, username, email, password_hash, first_name, last_name, role_id) VALUES 
('USR001', 'admin', 'admin@syos.com', '$2b$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewU5GYV4CJ2n2K1O', 'System', 'Administrator', 1);

-- Create default categories
INSERT INTO categories (category_code, category_name, description) VALUES 
('CAT001', 'Electronics', 'Electronic devices and accessories'),
('CAT002', 'Clothing', 'Apparel and fashion items'),
('CAT003', 'Books', 'Books and educational materials'),
('CAT004', 'Home & Garden', 'Home and garden products'),
('CAT005', 'Sports', 'Sports and fitness equipment');

-- Create default locations
INSERT INTO locations (location_code, location_name, location_type, capacity) VALUES 
('LOC001', 'Main Warehouse', 'WAREHOUSE', 10000),
('LOC002', 'Store Floor', 'STORE', 500),
('LOC003', 'Electronics Section', 'SECTION', 200),
('LOC004', 'Clothing Section', 'SECTION', 300);

-- Update location hierarchy
UPDATE locations SET parent_location_id = 2 WHERE location_code IN ('LOC003', 'LOC004');

-- Create test connection table (keep existing)
CREATE TABLE IF NOT EXISTS connection_test (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_message VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO connection_test (test_message) VALUES 
('SYOS Database setup completed successfully'),
('All tables created with proper relationships'),
('System ready for SYOS application');

-- Display success message
SELECT 'SYOS (Synex Outlet Store) Database setup completed successfully!' AS status;
SELECT 'Created tables:', COUNT(*) as table_count FROM information_schema.tables WHERE table_schema = 'syos_db';
SHOW TABLES;
