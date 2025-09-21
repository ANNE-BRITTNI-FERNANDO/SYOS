# SYOS Database Schema Documentation

## Overview
This document describes the complete database schema for the SYOS (Store Your Outstanding Stock) Point of Sale system.

## Database Tables Used

### 1. Users Table
**Purpose**: User authentication and role-based access control
```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_code VARCHAR(10) UNIQUE,           -- Auto-generated: EMP001, EMP002, etc.
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'MANAGER', 'USER') DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. Categories Table
**Purpose**: Hierarchical product categorization
```sql
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_category_id INT,                 -- NULL for main categories
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES categories(id)
);
```

### 3. Products Table
**Purpose**: Product catalog management with pricing and inventory
```sql
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    quantity INT DEFAULT 0,
    reorder_level INT DEFAULT 10,
    unit_of_measure VARCHAR(20) DEFAULT 'pcs',
    discount_amount DECIMAL(8,2) DEFAULT 0.00,
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    expiry_date DATE NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

### 4. Sales Table
**Purpose**: Transaction header information
```sql
CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_number VARCHAR(50) UNIQUE NOT NULL,    -- Auto-generated: SALE001, SALE002, etc.
    customer_name VARCHAR(255),                 -- Customer name (not ID reference)
    customer_email VARCHAR(255),
    customer_phone VARCHAR(20),
    subtotal DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    final_amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'DIGITAL') DEFAULT 'CASH',
    cash_received DECIMAL(10,2) DEFAULT 0.00,
    change_given DECIMAL(10,2) DEFAULT 0.00,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cashier_user_id INT,
    FOREIGN KEY (cashier_user_id) REFERENCES users(id)
);
```

### 5. Sale Items Table
**Purpose**: Individual items within each transaction
```sql
CREATE TABLE sale_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,    -- Stored for historical record
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,     -- Price at time of sale
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_price DECIMAL(10,2) NOT NULL,    -- Final line total after discount
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

## Key Features Implemented

### 1. Role-Based Access Control
- **ADMIN**: Full system access
- **MANAGER**: Limited access (restricted Product/Inventory Management + custom reports)
- **USER**: Basic POS operations

### 2. Automatic Code Generation
- User codes: EMP001, EMP002, EMP003...
- Sale numbers: SALE001, SALE002, SALE003...

### 3. Customer Management
- No separate customer table
- Customer information stored directly in sales table
- Supports walk-in customers and regular customers

### 4. Product Discount System
- Supports both amount-based and percentage-based discounts
- Automatic discount application in POS
- Manual override capability

### 5. Inventory Tracking
- Real-time stock levels
- Reorder level management
- Low stock indicators

## Sample Data Structure

### Categories (Hierarchical)
```
Electronics (Main Category)
├── Laptops (Subcategory)
├── Mobile Phones (Subcategory)
└── Audio Equipment (Subcategory)

Clothing (Main Category)
├── Men's Clothing (Subcategory)
├── Women's Clothing (Subcategory)
└── Kids' Clothing (Subcategory)
```

### Products (Sample)
- **HP Pavilion Laptop** - Electronics > Laptops - LKR 360,000
- **Samsung Galaxy S23** - Electronics > Mobile Phones - LKR 180,000
- **Sony Headphones** - Electronics > Audio - LKR 15,000
- **Men's T-Shirt** - Clothing > Men's - LKR 2,500 (10% discount)

### Users (Sample)
- **EMP001** - System Admin (ADMIN role)
- **EMP002** - Store Manager (MANAGER role)
- **EMP003** - Cashier User (USER role)

## System Reports Available

### 1. All System Reports (ADMIN only)
1. **Daily Sales Summary** - Sales performance by date
2. **Product Performance Report** - Best/worst selling products
3. **Inventory Levels Report** - Current stock status
4. **Low Stock Alert** - Products below reorder level
5. **Sales by Category** - Category-wise performance
6. **User Activity Report** - User login and transaction history

### 2. Manager Reports (MANAGER role)
1. **Daily Sales Summary** - Limited to operational data
2. **Inventory Levels Report** - Stock management focus

## Security Features

### 1. Password Management
- Passwords stored securely (BCrypt hashing in production)
- Default passwords for demo purposes

### 2. Role-Based Menu Restrictions
- ADMIN: Full access to all menus
- MANAGER: Restricted Product Management (4/7 options), Limited Inventory (4/5 options)
- USER: Basic POS operations only

### 3. Data Integrity
- Foreign key constraints
- Cascade delete for sale items
- Restrict delete for referenced products

## Performance Optimizations

### 1. Database Indexes
- Primary keys on all tables
- Foreign key indexes
- Search indexes on frequently queried fields

### 2. Connection Pooling
- HikariCP implementation
- Configurable pool size
- Connection lifecycle management

## Usage Notes

1. **Products**: Must be assigned to subcategories (not main categories)
2. **Sales**: Automatically generate unique sale numbers
3. **Inventory**: Real-time updates during sales transactions
4. **Discounts**: Can be applied at product level or individual sale level
5. **Customers**: Information captured per transaction, no registration required

## Future Enhancements

1. **Customer Registration System**: Loyalty programs and customer history
2. **Advanced Inventory**: Batch tracking, expiry management
3. **Multi-location Support**: Branch management and stock transfers
4. **Advanced Reporting**: Profit analysis, trend forecasting
5. **Integration APIs**: Third-party accounting systems, e-commerce platforms