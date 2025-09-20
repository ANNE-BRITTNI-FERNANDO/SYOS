# 🏪 SYOS - Store Your Outstanding Stock Management System

<div align="center">
  <img src="https://img.shields.io/badge/Java-11+-orange?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/MySQL-8.0+-blue?style=for-the-badge&logo=mysql" />
  <img src="https://img.shields.io/badge/Version-1.0.0-green?style=for-the-badge" />
  <img src="https://img.shields.io/badge/License-MIT-purple?style=for-the-badge" />
</div>

## 📖 Overview

SYOS (Store Your Outstanding Stock) is a comprehensive stock management system built with Java and MySQL. It features a complete role-based authentication system, intuitive CLI interface, and robust architecture designed for managing inventory, users, and business operations.

## ✨ Key Features

### 🔐 **Advanced Authentication System**
- **Role-Based Access Control**: Three-tier system (USER/MANAGER/ADMIN)
- **Secure Password Hashing**: SHA-256 + salt implementation
- **Session Management**: UUID-based tokens with audit logging
- **Real-time Validation**: Comprehensive input validation and security

### 👤 **User Management**
- **Multi-Role Support**: Customers, Employees, and Administrators
- **Admin Panel**: Create and manage users directly through interface
- **Profile Management**: User profile viewing and password changes
- **Audit Trail**: Complete authentication and action logging

### 🎯 **Professional Interface**
- **Branded CLI**: Beautiful ASCII art interface with professional design
- **Role-Based Menus**: Dynamic menu generation based on user permissions
- **Easy Launch**: Multiple ways to start the application
- **Error Handling**: Comprehensive error messages with troubleshooting tips

### 🏗️ **Robust Architecture**
- **Service Layer**: Clean separation of concerns
- **DAO Pattern**: Complete data access layer for all entities
- **Connection Pooling**: HikariCP for efficient database connections
- **Exception Handling**: Proper error management throughout the system

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- MySQL 8.0 or higher
- Maven dependencies (automatically handled)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/ANNE-BRITTNI-FERNANDO/SYOS.git
   cd SYOS
   ```

2. **Set up the database**
   ```bash
   # Create MySQL database 'syos_db'
   # Update src/main/resources/config.properties with your DB credentials
   
   # Run database setup
   java -cp "target/dependency/*;target/classes" syos.setup.DatabaseSetup
   ```

3. **Launch the application**
   ```bash
   # Option 1: Main class (Recommended)
   java -cp "target/dependency/*;target/classes" syos.Main
   
   # Option 2: Windows Batch File
   run-syos.bat
   
   # Option 3: PowerShell Script
   .\run-syos.ps1
   ```

## 🎮 Usage Guide

### 🔑 **Default Credentials**

#### Administrator Access
```
Email: admin@gmail.com
Password: Admin1234*
Role: ADMIN (Full system access)
```

#### Manager/Employee Access
```
Email: Employee1@gmail.com
Password: Employee1*
Role: MANAGER (Team management)
```

#### Customer Access
- Register through the CLI interface
- Automatically assigned USER role

### 📋 **Available Features by Role**

#### 🔧 **ADMIN Features**
- ✅ **Admin Panel**: Complete system administration
- ✅ **User Management**: View and manage all system users
- ✅ **Create Admin/Employee**: Add new admin and manager accounts
- ✅ **System Reports**: Comprehensive analytics and reporting
- ✅ **Full Access**: All system features and controls

#### 👔 **MANAGER Features**
- ✅ **Manager Panel**: Departmental management tools
- ✅ **Team View**: View and manage team members
- ✅ **Reports**: Access to departmental reports and metrics
- ✅ **Elevated Privileges**: Enhanced system access

#### 👤 **USER Features**
- ✅ **Profile Management**: View and edit personal information
- ✅ **Password Changes**: Secure password management
- ✅ **Basic Access**: Core system functionality

## 🏗️ **Project Structure**

```
SYOS/
├── src/main/java/
│   ├── syos/
│   │   ├── Main.java                    # 🎯 Application entry point
│   │   ├── cli/                         # 🖥️ Command-line interface
│   │   ├── services/                    # 🔧 Business logic layer
│   │   ├── dao/                         # 💾 Data access objects
│   │   ├── models/                      # 📊 Entity models
│   │   ├── admin/                       # 👨‍💼 Admin utilities
│   │   └── setup/                       # ⚙️ Database setup tools
│   └── com/syos/infrastructure/         # 🏗️ Core infrastructure
├── src/main/resources/                  # 📁 Configuration files
├── target/                              # 🔨 Compiled classes & dependencies
├── run-syos.bat                         # 🖥️ Windows launcher
├── run-syos.ps1                         # 💻 PowerShell launcher
└── USAGE.md                             # 📖 Detailed usage guide
```

## 🛠️ **Technical Architecture**

### **Database Schema**
- **users**: User accounts with role-based access
- **roles**: System roles (USER, MANAGER, ADMIN)
- **audit_logs**: Complete audit trail
- **products**: Inventory items
- **orders**: Purchase orders and transactions
- **categories**: Product categorization

### **Security Features**
- 🛡️ **Password Security**: SHA-256 + unique salt per user
- 🔐 **Session Management**: UUID tokens with expiration
- 📝 **Audit Logging**: Complete action tracking
- ✅ **Input Validation**: Comprehensive data validation
- 🎭 **Role-Based Access**: Three-tier permission system

### **Performance Features**
- ⚡ **Connection Pooling**: HikariCP for optimal database performance
- 🏗️ **Service Architecture**: Clean separation of concerns
- 🔄 **Transaction Management**: Proper database transaction handling
- 📊 **Efficient Queries**: Optimized database operations

## 📊 **Development Status**

- ✅ **Authentication System**: Complete with all security features
- ✅ **User Management**: Full CRUD operations with role support
- ✅ **CLI Interface**: Professional interface with role-based menus
- ✅ **Database Layer**: Complete DAO implementation
- 🚧 **Inventory Management**: Framework ready for implementation
- 🚧 **Order Processing**: Models and DAOs prepared
- 🚧 **Reporting System**: Basic structure in place

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
- Uses HikariCP for connection pooling
- Inspired by modern inventory management needs
- Designed for educational and commercial use

---

<div align="center">
  <b>🏪 SYOS v1.0.0 - Store Your Outstanding Stock 🏪</b><br>
  <i>Built with ❤️ for efficient stock management</i>
</div>
