package syos.cli;

import java.util.Scanner;

import com.syos.infrastructure.database.ConnectionPool;

import syos.dao.DAOFactory;
import syos.models.User;
import syos.models.Role;
import syos.services.AuthenticationService;
import syos.services.ServiceFactory;

/**
 * Simple CLI for testing the authentication system
 */
public class AuthenticationCLI {
    
    private final Scanner scanner;
    private final AuthenticationService authService;
    private User currentUser;
    private String currentSessionToken;
    
    public AuthenticationCLI() throws Exception {
        this.scanner = new Scanner(System.in);
        
        // Initialize connection pool
        ConnectionPool connectionPool = new ConnectionPool();
        
        // Initialize services
        DAOFactory daoFactory = DAOFactory.getInstance(connectionPool);
        ServiceFactory serviceFactory = ServiceFactory.getInstance(daoFactory);
        this.authService = serviceFactory.getAuthenticationService();
        
        System.out.println("Authentication CLI initialized successfully!");
    }
    
    public void start() {
        System.out.println("\n=== SYOS Authentication System ===");
        
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                showGuestMenu();
                int choice = getChoice();
                
                switch (choice) {
                    case 1:
                        handleRegistration();
                        break;
                    case 2:
                        handleLogin();
                        break;
                    case 3:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } else {
                showUserMenu();
                int choice = getChoice();
                String userRole = getUserRole();
                
                switch (choice) {
                    case 1:
                        showUserProfile();
                        break;
                    case 2:
                        handleChangePassword();
                        break;
                    case 3:
                        if ("ADMIN".equals(userRole)) {
                            handleAdminPanel();
                        } else if ("MANAGER".equals(userRole)) {
                            handleManagerPanel();
                        } else {
                            // USER role - logout
                            handleLogout();
                        }
                        break;
                    case 4:
                        if ("ADMIN".equals(userRole)) {
                            handleUserManagement();
                        } else if ("MANAGER".equals(userRole)) {
                            handleViewTeam();
                        } else {
                            // USER role - exit
                            running = false;
                        }
                        break;
                    case 5:
                        if ("ADMIN".equals(userRole)) {
                            handleCreateAdminEmployee();
                        } else if ("MANAGER".equals(userRole)) {
                            handleReports();
                        } else {
                            System.out.println("Invalid choice! Please try again.");
                        }
                        break;
                    case 6:
                        if ("ADMIN".equals(userRole)) {
                            handleSystemReports();
                        } else if ("MANAGER".equals(userRole)) {
                            handleLogout();
                        } else {
                            System.out.println("Invalid choice! Please try again.");
                        }
                        break;
                    case 7:
                        if ("ADMIN".equals(userRole)) {
                            handleLogout();
                        } else if ("MANAGER".equals(userRole)) {
                            running = false;
                        } else {
                            System.out.println("Invalid choice! Please try again.");
                        }
                        break;
                    case 8:
                        if ("ADMIN".equals(userRole)) {
                            running = false;
                        } else {
                            System.out.println("Invalid choice! Please try again.");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            }
        }
        
        cleanup();
        System.out.println("Goodbye!");
    }
    
    private void showGuestMenu() {
        System.out.println("\n--- Authentication Menu ---");
        System.out.println("1. Register new user");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }
    
    private void showUserMenu() {
        try {
            // Get user's role to determine menu options
            String userRole = getUserRole();
            String displayName = currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername();
            
            System.out.println("\n--- " + userRole + " Menu (Logged in as: " + displayName + ") ---");
            System.out.println("1. View Profile");
            System.out.println("2. Change Password");
            
            // Role-specific menu options
            if ("ADMIN".equals(userRole)) {
                System.out.println("3. Admin Panel");
                System.out.println("4. User Management");
                System.out.println("5. Create Admin/Employee");
                System.out.println("6. System Reports");
                System.out.println("7. Logout");
                System.out.println("8. Exit");
            } else if ("MANAGER".equals(userRole)) {
                System.out.println("3. Manager Panel");
                System.out.println("4. View Team");
                System.out.println("5. Reports");
                System.out.println("6. Logout");
                System.out.println("7. Exit");
            } else {
                // USER role
                System.out.println("3. Logout");
                System.out.println("4. Exit");
            }
            
            System.out.print("Choose an option: ");
        } catch (Exception e) {
            System.out.println("Error displaying menu: " + e.getMessage());
            System.out.print("Choose an option: ");
        }
    }
    
    private String getUserRole() {
        try {
            if (currentUser == null || currentUser.getRoleId() == null) {
                return "USER";
            }
            
            // Get role name from roleId
            DAOFactory daoFactory = DAOFactory.getInstance(new ConnectionPool());
            Role role = daoFactory.getRoleDAO().findById(currentUser.getRoleId()).orElse(null);
            
            if (role != null) {
                return role.getName();
            }
            return "USER";
        } catch (Exception e) {
            return "USER";
        }
    }
    
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void handleRegistration() {
        System.out.println("\n--- User Registration ---");
        
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("Password (min 8 chars, include uppercase, lowercase, digit, special char): ");
            String password = scanner.nextLine();
            
            System.out.print("Confirm Password: ");
            String confirmPassword = scanner.nextLine();
            
            System.out.print("First Name: ");
            String firstName = scanner.nextLine().trim();
            
            System.out.print("Last Name (optional): ");
            String lastName = scanner.nextLine().trim();
            if (lastName.isEmpty()) {
                lastName = null;
            }
            
            System.out.print("Phone (optional): ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty()) {
                phone = null;
            }
            
            AuthenticationService.RegistrationResult result = 
                authService.registerUser(email, username, password, confirmPassword, firstName, lastName, phone);
            
            if (result.isSuccess()) {
                System.out.println("✓ Registration successful! You can now login.");
                System.out.println("User ID: " + result.getUser().getId());
                System.out.println("Email: " + result.getUser().getEmail());
            } else {
                System.out.println("✗ Registration failed: " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("✗ Registration error: " + e.getMessage());
        }
    }
    
    private void handleLogin() {
        System.out.println("\n--- User Login ---");
        
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            
            System.out.print("Password: ");
            String password = scanner.nextLine();
            
            AuthenticationService.LoginResult result = authService.authenticateUser(email, password);
            
            if (result.isSuccess()) {
                currentUser = result.getUser();
                currentSessionToken = result.getSessionToken();
                System.out.println("✓ Login successful! Welcome, " + currentUser.getFullName());
                System.out.println("Session Token: " + currentSessionToken.substring(0, 8) + "...");
            } else {
                System.out.println("✗ Login failed: " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("✗ Login error: " + e.getMessage());
        }
    }
    
    private void showUserProfile() {
        System.out.println("\n--- User Profile ---");
        System.out.println("ID: " + currentUser.getId());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Name: " + currentUser.getFullName());
        System.out.println("Phone: " + (currentUser.getPhone() != null ? currentUser.getPhone() : "Not provided"));
        System.out.println("Role ID: " + currentUser.getRoleId());
        System.out.println("Active: " + (currentUser.isActive() ? "Yes" : "No"));
        System.out.println("Created: " + currentUser.getCreatedAt());
        System.out.println("Last Login: " + (currentUser.getLastLogin() != null ? currentUser.getLastLogin() : "N/A"));
    }
    
    private void handleChangePassword() {
        System.out.println("\n--- Change Password ---");
        
        try {
            System.out.print("Current Password: ");
            String currentPassword = scanner.nextLine();
            
            System.out.print("New Password: ");
            String newPassword = scanner.nextLine();
            
            System.out.print("Confirm New Password: ");
            String confirmPassword = scanner.nextLine();
            
            boolean success = authService.changePassword(
                currentUser.getId(), currentPassword, newPassword, confirmPassword);
            
            if (success) {
                System.out.println("✓ Password changed successfully! Please login again.");
                handleLogout();
            } else {
                System.out.println("✗ Password change failed. Please check your current password and try again.");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Password change error: " + e.getMessage());
        }
    }
    
    private void handleLogout() {
        try {
            if (currentSessionToken != null) {
                boolean loggedOut = authService.logout(currentSessionToken);
                if (loggedOut) {
                    System.out.println("✓ Logged out successfully.");
                } else {
                    System.out.println("! Session already expired or invalid.");
                }
            }
            
            currentUser = null;
            currentSessionToken = null;
            
        } catch (Exception e) {
            System.out.println("✗ Logout error: " + e.getMessage());
            // Force cleanup anyway
            currentUser = null;
            currentSessionToken = null;
        }
    }
    
    private void cleanup() {
        if (currentUser != null) {
            handleLogout();
        }
        
        try {
            ServiceFactory.getInstance().cleanup();
        } catch (Exception e) {
            System.out.println("Cleanup error: " + e.getMessage());
        }
        
        scanner.close();
    }
    
    // Role-specific handler methods
    private void handleAdminPanel() {
        System.out.println("\n=== ADMIN PANEL ===");
        System.out.println("Welcome to the admin panel!");
        System.out.println("- Manage all users in the system");
        System.out.println("- Configure system settings");
        System.out.println("- View comprehensive reports");
        System.out.println("- Full access to all features");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void handleManagerPanel() {
        System.out.println("\n=== MANAGER PANEL ===");
        System.out.println("Welcome to the manager panel!");
        System.out.println("- View and manage your team");
        System.out.println("- Access departmental reports");
        System.out.println("- Approve/manage transactions");
        System.out.println("- Limited administrative functions");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void handleUserManagement() {
        System.out.println("\n=== USER MANAGEMENT ===");
        try {
            DAOFactory daoFactory = DAOFactory.getInstance(new ConnectionPool());
            java.util.List<User> users = daoFactory.getUserDAO().findAll();
            
            System.out.println("All Users in System:");
            System.out.println("ID | Email | Name | Role | Status");
            System.out.println("---|-------|------|------|-------");
            
            for (User user : users) {
                String roleName = "Unknown";
                if (user.getRoleId() != null) {
                    Role role = daoFactory.getRoleDAO().findById(user.getRoleId()).orElse(null);
                    if (role != null) {
                        roleName = role.getName();
                    }
                }
                String status = user.isActive() ? "Active" : "Inactive";
                System.out.printf("%d | %s | %s | %s | %s%n", 
                    user.getId(), user.getEmail(), user.getFullName(), roleName, status);
            }
        } catch (Exception e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void handleViewTeam() {
        System.out.println("\n=== TEAM VIEW ===");
        System.out.println("View your team members and their activities");
        System.out.println("(This would show employees under this manager)");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void handleSystemReports() {
        System.out.println("\n=== SYSTEM REPORTS ===");
        System.out.println("📊 System Activity Reports");
        System.out.println("- User registration trends");
        System.out.println("- Login activity analysis");
        System.out.println("- Security audit logs");
        System.out.println("- Performance metrics");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void handleReports() {
        System.out.println("\n=== MANAGER REPORTS ===");
        System.out.println("📈 Departmental Reports");
        System.out.println("- Team performance metrics");
        System.out.println("- Department activity summary");
        System.out.println("- Resource utilization");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void handleCreateAdminEmployee() {
        System.out.println("\n=== CREATE ADMIN/EMPLOYEE USER ===");
        
        try {
            // Show role options
            System.out.println("Select role for new user:");
            System.out.println("1. ADMIN (Full system access)");
            System.out.println("2. MANAGER (Employee/Department manager)");
            System.out.println("3. Cancel");
            System.out.print("Choose role: ");
            
            int roleChoice = getChoice();
            if (roleChoice == 3) {
                System.out.println("User creation cancelled.");
                return;
            }
            
            String roleName;
            String roleDescription;
            switch (roleChoice) {
                case 1:
                    roleName = "ADMIN";
                    roleDescription = "Administrator with full system access";
                    break;
                case 2:
                    roleName = "MANAGER";
                    roleDescription = "Manager with elevated privileges";
                    break;
                default:
                    System.out.println("Invalid role selection!");
                    return;
            }
            
            System.out.println("\n--- Creating " + roleName + " User ---");
            System.out.println("Role: " + roleDescription);
            System.out.println();
            
            // Get user details
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("Password (min 8 chars, include uppercase, lowercase, digit, special char): ");
            String password = scanner.nextLine();
            
            System.out.print("Confirm Password: ");
            String confirmPassword = scanner.nextLine();
            
            System.out.print("First Name: ");
            String firstName = scanner.nextLine().trim();
            
            System.out.print("Last Name (optional): ");
            String lastName = scanner.nextLine().trim();
            if (lastName.isEmpty()) lastName = null;
            
            System.out.print("Phone (optional): ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty()) phone = null;
            
            // Create the user
            System.out.println("\nCreating user account...");
            AuthenticationService.RegistrationResult result = authService.registerUser(
                email, username, password, confirmPassword, firstName, lastName, phone
            );
            
            if (result.isSuccess()) {
                // Update the user's role
                User newUser = result.getUser();
                
                // Get the role ID
                DAOFactory daoFactory = DAOFactory.getInstance(new ConnectionPool());
                Role role = daoFactory.getRoleDAO().findByName(roleName);
                
                if (role != null) {
                    newUser.setRoleId(role.getId());
                    daoFactory.getUserDAO().update(newUser);
                    
                    System.out.println("\n✅ " + roleName + " user created successfully!");
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    System.out.println("📧 Email: " + email);
                    System.out.println("👤 Username: " + username);
                    System.out.println("🎭 Role: " + roleName + " (ID: " + role.getId() + ")");
                    System.out.println("📅 Created: " + java.time.LocalDateTime.now().toString());
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    
                    System.out.println("\n🔐 Login Credentials:");
                    System.out.println("Email: " + email);
                    System.out.println("Password: [As entered above]");
                    
                } else {
                    System.out.println("❌ Error: Could not find " + roleName + " role in database!");
                }
                
            } else {
                System.out.println("❌ Failed to create user: " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error creating user: " + e.getMessage());
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    public static void main(String[] args) {
        try {
            AuthenticationCLI cli = new AuthenticationCLI();
            cli.start();
        } catch (Exception e) {
            System.err.println("Failed to start CLI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}