# 🏪 SYOS - Store Your Outstanding Stock Management System

<div align="center">
  <img src="https://img.shields.io/badge/Java-11+-orange?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/MySQL-8.0+-blue?style=for-the-badge&logo=mysql" />
  <img src="https://img.shields.io/badge/Version-2.0.0-green?style=for-the-badge" />
  <img src="https://img.shields.io/badge/License-MIT-purple?style=for-the-badge" />
</div>

## 📖 Overview

SYOS (Store Your Outstanding Stock) is a comprehensive Point of Sale (POS) and inventory management system built with Java and MySQL. It features a complete role-based authentication system, intuitive CLI interface, real-time inventory tracking, and robust architecture designed for retail operations, inventory management, and business reporting.

**Current System Status**: ✅ **Fully Operational**
- Complete POS system with transaction processing
- Real-time inventory management with automatic stock updates
- Role-based access control (ADMIN/MANAGER/USER)
- Comprehensive reporting system
- Customer management and sales tracking

## ✨ Key Features

### 🔐 **Advanced Authentication System**
- **Role-Based Access Control**: Three-tier system (ADMIN/CASHIER/CUSTOMER)
- **Secure Password Storage**: Industry-standard password hashing
- **Session Management**: Secure user session handling
- **User Registration**: Self-service customer registration

### 🗂️ **Hierarchical Category System with Smart Defaults**
- **Two-Level Hierarchy**: Main categories (organizational) + Subcategories (product containers)
- **Smart Shelf Defaults**: Each subcategory has configurable default shelf capacity and minimum thresholds
- **Category-Based Product Management**: Products can only be added to subcategories
- **Quick Default Options**: One-click application of category defaults during product creation
- **Flexible Override**: Custom shelf values can still be set per product when needed

### � **Advanced Product Management**
- **Product-Specific Shelf Management**: Individual shelf capacity and minimum threshold per product
- **Hierarchical Product Codes**: Format follows PARENT-SUB-001 structure (e.g., EL-LAPTOP-001)
- **Category-Based Defaults**: Intelligent defaults based on subcategory (Books: 5/1, Electronics: 15/3, etc.)
- **Validation**: Prevents adding products to main categories (organizational containers only)
- **Auto-Restock Settings**: Configurable automatic restocking when below thresholds

### 🏪 **Complete POS System**
- **Sales Processing**: Full point-of-sale functionality
- **Inventory Integration**: Real-time stock updates with sales
- **FIFO Stock Management**: First-In-First-Out inventory tracking
- **Bill Generation**: Professional receipt generation
- **Payment Methods**: Multiple payment option support

### 🎯 **Professional Interface**
- **Branded CLI**: Beautiful ASCII art interface with professional design
- **Role-Based Menus**: Dynamic menu generation based on user permissions
- **Category Tree Display**: Visual hierarchical category display with folder icons
- **Easy Navigation**: Intuitive code-based selection (no confusing IDs)
- **Error Handling**: Comprehensive error messages with troubleshooting tips

### 🏗️ **Robust Architecture**
- **Service Layer**: Clean separation of concerns
- **Connection Pooling**: Efficient database connection management
- **Audit Trails**: Complete tracking of inventory movements and user actions
- **Performance Optimized**: Indexed database queries and efficient data structures

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- MySQL 8.0 or higher
- MySQL Connector/J (included in lib/ folder)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/ANNE-BRITTNI-FERNANDO/SYOS.git
   cd SYOS
   ```

2. **Set up the database**
   ```sql
   -- Import the complete database schema
   mysql -u root -p < syos_complete_schema.sql
   ```
   
   Or run the SQL file in your MySQL client/phpMyAdmin

3. **Configure database connection**
   Update `src/main/resources/config.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/syos_db
   db.username=your_username
   db.password=your_password
   ```

4. **Launch the application**
   ```bash
   # Compile and run
   javac -cp "lib/*" src/main/java/syos/*.java
   java -cp "src/main/java;lib/*" syos.Main
   ```

## 🎮 Usage Guide

### 🔑 **Default Credentials**

#### Administrator Access
```
Email: Admin@gmail.com
Password: Admin1234*
Role: ADMIN (Full system access)
```

### 📋 **Category Management System**

#### 🗂️ **Creating Categories**

**Main Categories** (Organizational containers):
- Purpose: Group related subcategories
- Examples: Electronics, Clothing, Books, Sports
- Shelf Defaults: Not applicable (products cannot be added here)
- Message: "Main category created - products will be added to subcategories under this category."

**Subcategories** (Where products are stored):
- Purpose: Actual product containers with smart defaults
- Examples: Laptops, Mobile Phones, Men's Clothing, Fiction Books
- Shelf Defaults: Required (e.g., Laptops: 15 capacity/3 threshold)
- Products: Can only be added to subcategories

#### 📦 **Adding Products**

1. **Subcategory Selection**: System displays hierarchical categories and enforces subcategory selection
2. **Validation**: Prevents adding products to main categories with clear error messages
3. **Smart Defaults**: 
   - Option 1: "Use Subcategory Default (15/3)" ← **One-click efficiency!**
   - Option 2: "Custom Values" ← Manual entry with suggested defaults
4. **Hierarchical Codes**: Auto-generates codes like EL-LAPTOP-001, CL-MENS-001

### 🏷️ **Sample Category Structure**

```
📂 Electronics (CAT001) - Main Category
├── 📱 Laptops (LAPTOP) - Default: 15 capacity/3 threshold
├── 📱 Mobile Phones (MOBILE) - Default: 15 capacity/3 threshold
└── 🎧 Audio & Headphones (AUDIO) - Default: 15 capacity/3 threshold

📂 Clothing (CAT002) - Main Category  
├── 👔 Men's Clothing (MENS) - Default: 20 capacity/5 threshold
├── 👗 Women's Clothing (WOMENS) - Default: 20 capacity/5 threshold
└── 👶 Kids Clothing (KIDS) - Default: 20 capacity/5 threshold

📂 Books (CAT003) - Main Category
├── 📚 Fiction Books (FICTION) - Default: 5 capacity/1 threshold
├── 📖 Technical Books (TECH) - Default: 5 capacity/1 threshold
└── 📘 Educational Books (EDUC) - Default: 5 capacity/1 threshold
```

### � **Available Features by Role**

#### 🔧 **ADMIN Features**
- ✅ **User Management**: Complete user administration
- ✅ **Product Management**: Full product and category control
- ✅ **Inventory Management**: Stock tracking and management
- ✅ **POS Terminal**: Complete sales processing
- ✅ **System Reports**: Comprehensive analytics
- ✅ **System Configuration**: Full system control

#### � **CASHIER Features**
- ✅ **POS Terminal**: Sales processing and checkout
- ✅ **Product Lookup**: Search and view products
- ✅ **Inventory View**: Check stock levels
- ✅ **Customer Management**: Handle customer transactions

#### 👤 **CUSTOMER Features**
- ✅ **Product Browsing**: View available products
- ✅ **Profile Management**: Manage personal information
- ✅ **Order History**: View past purchases

## 🏗️ **Project Structure**

```
SYOS/
├── src/main/java/syos/
│   ├── Main.java                        # 🎯 Application entry point with full POS system
│   ├── SimpleConnectionPool.java        # 💾 Database connection management
│   └── util/                           # 🔧 Database utilities
├── src/main/resources/
│   ├── config.properties               # ⚙️ Database configuration
│   └── sql/                           # � SQL scripts and utilities
├── lib/                               # � External libraries (MySQL Connector)
├── syos_complete_schema.sql           # �️ Complete database schema
└── README.md                          # 📖 This documentation
```

## 🛠️ **Technical Architecture**

### **Database Schema Features**
- **Hierarchical Categories**: Two-level structure with parent-child relationships
- **Smart Defaults**: Category-based default shelf capacity and minimum thresholds
- **Product Constraints**: Database-level validation ensuring products only in subcategories
- **FIFO Inventory**: First-In-First-Out stock batch tracking
- **Audit Trails**: Complete stock movement history
- **Performance Indexes**: Optimized for fast queries

### **Category-Based Shelf Management**
```sql
-- Main categories: organizational only (default_shelf_capacity = 0)
-- Subcategories: have actual defaults for products
-- Products: inherit subcategory defaults with override options
```

### **Smart Default Examples**
- **Books Categories**: Conservative defaults (5 capacity / 1 threshold)
- **Electronics Categories**: Moderate defaults (15 capacity / 3 threshold)  
- **Clothing Categories**: High defaults (20 capacity / 5 threshold)
- **Sports Categories**: Moderate defaults (8 capacity / 2 threshold)

### **Security Features**
- 🛡️ **Role-Based Access**: Hierarchical permission system
- 🔐 **Secure Authentication**: Password hashing and session management
- 📝 **Audit Logging**: Complete user action tracking
- ✅ **Input Validation**: Comprehensive data validation
- 🎭 **Business Logic Validation**: Prevents illogical operations

## 📊 **Development Status**

- ✅ **Authentication System**: Complete with role-based access
- ✅ **Hierarchical Category System**: Complete with smart defaults
- ✅ **Product Management**: Complete with category-based defaults and validation
- ✅ **Inventory Management**: Complete with FIFO stock tracking
- ✅ **POS System**: Complete with sales processing and stock updates
- ✅ **Database Schema**: Complete with all tables, constraints, and views
- ✅ **CLI Interface**: Professional interface with hierarchical navigation

## 🎯 **Key Innovations**

1. **Hierarchical Category Logic**: Clear separation between organizational containers and product storage
2. **Smart Default System**: Category-based intelligent defaults with one-click application
3. **Validation Layer**: Prevents logical errors (products in main categories)
4. **User-Friendly Codes**: Meaningful category codes instead of confusing IDs
5. **Flexible Override**: Balance between efficiency and customization

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 **Author**

**ANNE BRITTNI FERNANDO**
- GitHub: [@ANNE-BRITTNI-FERNANDO](https://github.com/ANNE-BRITTNI-FERNANDO)

## 🙏 **Acknowledgments**

- Built with Java and MySQL
- Features hierarchical category management with smart defaults
- Designed for modern retail and inventory management needs
- Optimized for educational and commercial use

---

<div align="center">
  <b>🏪 SYOS v2.0.0 - Store Your Outstanding Stock 🏪</b><br>
  <i>Built with ❤️ for efficient stock management with smart category defaults</i>
</div>
