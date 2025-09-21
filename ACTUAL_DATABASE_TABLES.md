# SYOS - Actual Database Tables Used

## Overview
This document lists the **ACTUAL** database tables used by the SYOS POS system as implemented and tested.

---

## ✅ **Tables Currently Used by the System**

### 1. **`users`** - User Management & Authentication
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
**Usage**: Complete user authentication, role-based access control, automatic user code generation

---

### 2. **`categories`** - Product Categories
```sql
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_category_id INT,                 -- For hierarchical categories
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES categories(id)
);
```
**Usage**: Hierarchical category management (main categories + subcategories)

---

### 3. **`products`** - Product Catalog
```sql
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    quantity INT DEFAULT 0,                 -- Current stock level
    reorder_level INT DEFAULT 10,           -- Low stock threshold
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
**Usage**: Complete product management with pricing, inventory levels, and discount support

---

### 4. **`sales`** - Transaction Headers
```sql
CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_number VARCHAR(50) UNIQUE NOT NULL,    -- Auto-generated: SALE001, SALE002, etc.
    customer_name VARCHAR(255),                 -- Customer name (direct storage)
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
**Usage**: Main transaction records with customer info, payment details, and automatic sale number generation

---

### 5. **`sale_items`** - Transaction Line Items
```sql
CREATE TABLE sale_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,        -- Stored for historical record
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,         -- Price at time of sale
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_price DECIMAL(10,2) NOT NULL,        -- Final line total after discount
    FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```
**Usage**: Individual items within each transaction, maintains price history for reporting

---

## 🔧 **System Features Using These Tables**

### ✅ **Currently Working Features**

1. **User Management**
   - Auto-generated user codes (EMP001, EMP002, etc.)
   - Role-based access control (ADMIN/MANAGER/USER)
   - User creation, editing, deletion

2. **Product Management**
   - Hierarchical categories (main categories + subcategories)
   - Product catalog with pricing and inventory
   - Discount management (amount and percentage)
   - Stock level tracking

3. **Point of Sale (POS)**
   - Complete transaction processing
   - Shopping cart functionality
   - Real-time inventory updates
   - Automatic sale number generation (SALE001, SALE002, etc.)
   - Customer information capture

4. **Inventory Management**
   - Real-time stock updates during sales
   - Low stock alerts
   - Stock level reports
   - Quantity tracking

5. **Reporting System**
   - Daily Sales Summary
   - Product Performance Report
   - Inventory Levels Report
   - Low Stock Alerts
   - Sales by Category
   - User Activity Report

6. **Customer Management**
   - Customer information storage per transaction
   - Transaction history
   - Customer lookup by name/email/phone

### ✅ **Proven Functionality (Tested & Working)**

- **Database Connection**: HikariCP connection pooling ✅
- **User Authentication**: Login system with role validation ✅
- **Product Catalog**: Full CRUD operations ✅
- **Sales Processing**: Complete POS transactions ✅
- **Inventory Updates**: Real-time stock adjustments ✅
- **Report Generation**: All 6 report types functional ✅
- **Role Restrictions**: MANAGER role properly limited ✅

---

## 📊 **Sample Data Structure**

### Users
```
EMP001 | System Admin     | ADMIN   | admin@syos.com
EMP002 | Store Manager    | MANAGER | manager@syos.com  
EMP003 | Cashier User     | USER    | cashier@syos.com
```

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
```
ID | Name              | Category        | Price      | Stock | Discount
1  | HP Pavilion       | Laptops         | 360,000.00 | 10    | 0%
2  | Samsung Galaxy    | Mobile Phones   | 180,000.00 | 15    | 5%
3  | Sony Headphones   | Audio Equipment | 15,000.00  | 25    | 10%
4  | Men's T-Shirt     | Men's Clothing  | 2,500.00   | 50    | 0%
```

### Sales Transactions
```
SALE001 | John Doe     | 2024-09-21 | 372,500.00 | CASH
SALE002 | Jane Smith   | 2024-09-21 | 15,000.00  | CARD
SALE003 | Walk-in      | 2024-09-21 | 2,500.00   | CASH
```

---

## 🎯 **Production Ready Features**

✅ **Database Design**: All tables have proper constraints and relationships  
✅ **Transaction Safety**: ACID compliance with rollback support  
✅ **Performance**: Indexed queries for fast response  
✅ **Data Integrity**: Foreign key constraints prevent orphaned records  
✅ **Audit Trail**: Complete transaction history  
✅ **Scalability**: Connection pooling for multiple users  

---

## 📝 **Usage Notes**

1. **Auto-Generation**: User codes and sale numbers are automatically generated
2. **Customer Storage**: Customer info is stored directly in sales table (no separate customer table needed)
3. **Historical Pricing**: Product names and prices are stored in sale_items for historical accuracy
4. **Role Restrictions**: MANAGER users have limited menu access (4/7 Product options, 4/5 Inventory options)
5. **Real-time Updates**: Stock levels update immediately during sales transactions

---

**System Status**: ✅ **Fully Operational & Production Ready**