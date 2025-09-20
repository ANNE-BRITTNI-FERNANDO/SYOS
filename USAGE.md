# SYOS - Store Your Outstanding Stock Management System

## 🚀 Quick Start

### Option 1: Using Main.java (Recommended)
```bash
java -cp "target/dependency/*;target/classes" syos.Main
```

### Option 2: Using Batch File (Windows)
Double-click `run-syos.bat` or run:
```bash
run-syos.bat
```

### Option 3: Direct CLI Access
```bash
java -cp "target/dependency/*;target/classes" syos.cli.AuthenticationCLI
```

## 🔐 User Accounts & Access Levels

### 👤 Customer/User Access
- **Registration**: Use the "Register new user" option in the CLI
- **Role**: USER (automatically assigned)
- **Access**: Basic profile management, password changes

### 👔 Manager/Employee Access
- **Login**: `Employee1@gmail.com` / `Employee1*`
- **Login**: `Employee2@gmail.com` / `Employee2*`
- **Role**: MANAGER
- **Access**: Team management, departmental reports, elevated privileges

### 🔧 Administrator Access
- **Login**: `admin@gmail.com` / `Admin1234*`
- **Role**: ADMIN
- **Access**: Full system control, user management, system reports

## 🛠️ System Requirements

### Database
- MySQL 8.0 or higher
- Database name: `syos_db`
- Default credentials: root/12345

### Java
- Java 11 or higher
- Required JARs in `target/dependency/`:
  - MySQL Connector
  - HikariCP
  - SLF4J

## 📋 Available Features

### For All Users
- ✅ Secure login/logout
- ✅ Profile management
- ✅ Password changes
- ✅ Session management

### For Managers
- ✅ Manager Panel
- ✅ Team view
- ✅ Departmental reports
- ✅ Limited admin functions

### For Administrators
- ✅ Admin Panel
- ✅ User Management (view all users)
- ✅ System Reports
- ✅ Full system access

## 🔧 Setup Instructions

1. **Database Setup**:
   ```bash
   java -cp "target/dependency/*;target/classes" syos.setup.DatabaseSetup
   ```

2. **Create Admin Users**:
   ```bash
   java -cp "target/dependency/*;target/classes" syos.admin.AdminSetup
   ```

3. **Run Application**:
   ```bash
   java -cp "target/dependency/*;target/classes" syos.Main
   ```

## 🏗️ Project Structure

```
SYOS - Test/
├── src/main/java/
│   ├── syos/
│   │   ├── Main.java              # 🎯 Main entry point
│   │   ├── cli/
│   │   │   └── AuthenticationCLI.java
│   │   ├── services/
│   │   │   └── AuthenticationService.java
│   │   ├── dao/
│   │   ├── models/
│   │   └── admin/
│   └── com/syos/infrastructure/
├── target/
│   ├── classes/                   # Compiled Java classes
│   └── dependency/               # JAR dependencies
├── run-syos.bat                  # Windows launcher
└── README.md                     # This file
```

## 💡 Usage Examples

### Customer Registration & Login
```
1. Run: java -cp "target/dependency/*;target/classes" syos.Main
2. Choose: 1 (Register new user)
3. Enter: email, username, password, name
4. Login with your credentials
5. Access: User menu with profile options
```

### Admin Login & User Management
```
1. Run: java -cp "target/dependency/*;target/classes" syos.Main
2. Choose: 2 (Login)
3. Enter: admin@gmail.com / Admin1234*
4. Access: Admin panel with user management
5. View: All users in the system
```

### Manager Login & Team View
```
1. Run: java -cp "target/dependency/*;target/classes" syos.Main
2. Choose: 2 (Login)
3. Enter: Employee1@gmail.com / Employee1*
4. Access: Manager panel with team features
5. View: Departmental reports and team data
```

## 🔒 Security Features

- 🛡️ **Password Security**: SHA-256 + salt hashing
- 🔐 **Session Management**: UUID-based tokens
- 📝 **Audit Logging**: Complete authentication tracking
- 🎭 **Role-Based Access**: Three-tier permission system
- ✅ **Input Validation**: Comprehensive data validation

## 📞 Support

For issues or questions:
1. Check the troubleshooting tips displayed on startup errors
2. Verify database connectivity
3. Ensure all dependencies are in place
4. Review the console output for specific error messages

---

**Version**: 1.0.0  
**Build Date**: September 20, 2025  
**Author**: SYOS Development Team