package syos.admin;

import com.syos.infrastructure.database.ConnectionPool;
import syos.dao.DAOFactory;
import syos.dao.RoleDAO;
import syos.dao.UserDAO;
import syos.models.Role;
import syos.models.User;
import syos.services.AuthenticationService;
import syos.services.ServiceFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Admin utility for managing users and checking system status
 */
public class AdminSetup {
    
    public static void main(String[] args) {
        ConnectionPool connectionPool = null;
        try {
            connectionPool = new ConnectionPool();
            DAOFactory daoFactory = DAOFactory.getInstance(connectionPool);
            ServiceFactory serviceFactory = ServiceFactory.getInstance(daoFactory);
            
            RoleDAO roleDAO = daoFactory.getRoleDAO();
            UserDAO userDAO = daoFactory.getUserDAO();
            AuthenticationService authService = serviceFactory.getAuthenticationService();
            
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("=== SYOS Admin Setup Utility ===\n");
            
            // Show existing roles
            System.out.println("Current Roles in System:");
            List<Role> roles = roleDAO.findAll();
            for (Role role : roles) {
                System.out.println("- ID: " + role.getId() + " | Name: " + role.getName() + 
                                 " | Description: " + role.getDescription());
            }
            
            System.out.println("\nCurrent Users in System:");
            List<User> users = userDAO.findAll();
            for (User user : users) {
                String roleName = "Unknown";
                if (user.getRoleId() != null) {
                    Role userRole = roleDAO.findById(user.getRoleId()).orElse(null);
                    if (userRole != null) {
                        roleName = userRole.getName();
                    }
                }
                System.out.println("- ID: " + user.getId() + " | Email: " + user.getEmail() + 
                                 " | Name: " + user.getFullName() + " | Role: " + roleName + 
                                 " | Active: " + user.isActive());
            }
            
            System.out.println("\n=== Create Admin User ===");
            System.out.print("Enter admin email: ");
            String adminEmail = scanner.nextLine().trim();
            System.out.print("Enter admin username: ");
            String adminUsername = scanner.nextLine().trim();
            System.out.print("Enter admin password: ");
            String adminPassword = scanner.nextLine().trim();
            System.out.print("Enter admin first name: ");
            String adminFirstName = scanner.nextLine().trim();
            
            // Find ADMIN role
            Role adminRole = null;
            for (Role role : roles) {
                if ("ADMIN".equals(role.getName())) {
                    adminRole = role;
                    break;
                }
            }
            
            if (adminRole == null) {
                System.out.println("❌ ADMIN role not found in database!");
                return;
            }
            
            // Create admin user
            AuthenticationService.RegistrationResult result = authService.registerUser(
                adminEmail, adminUsername, adminPassword, adminPassword,
                adminFirstName, null, null
            );
            
            if (result.isSuccess()) {
                // Update the user's role to ADMIN
                User adminUser = result.getUser();
                adminUser.setRoleId(adminRole.getId());
                userDAO.update(adminUser);
                
                System.out.println("✅ Admin user created successfully!");
                System.out.println("Email: " + adminEmail);
                System.out.println("Username: " + adminUsername);
                System.out.println("Role: ADMIN (ID: " + adminRole.getId() + ")");
            } else {
                System.out.println("❌ Failed to create admin user: " + result.getMessage());
            }
            
            System.out.println("\n=== Create Manager/Employee User ===");
            System.out.print("Enter employee email: ");
            String empEmail = scanner.nextLine().trim();
            System.out.print("Enter employee username: ");
            String empUsername = scanner.nextLine().trim();
            System.out.print("Enter employee password: ");
            String empPassword = scanner.nextLine().trim();
            System.out.print("Enter employee first name: ");
            String empFirstName = scanner.nextLine().trim();
            
            // Find MANAGER role
            Role managerRole = null;
            for (Role role : roles) {
                if ("MANAGER".equals(role.getName())) {
                    managerRole = role;
                    break;
                }
            }
            
            if (managerRole == null) {
                System.out.println("❌ MANAGER role not found in database!");
                return;
            }
            
            // Create manager user
            AuthenticationService.RegistrationResult empResult = authService.registerUser(
                empEmail, empUsername, empPassword, empPassword,
                empFirstName, null, null
            );
            
            if (empResult.isSuccess()) {
                // Update the user's role to MANAGER
                User empUser = empResult.getUser();
                empUser.setRoleId(managerRole.getId());
                userDAO.update(empUser);
                
                System.out.println("✅ Employee/Manager user created successfully!");
                System.out.println("Email: " + empEmail);
                System.out.println("Username: " + empUsername);
                System.out.println("Role: MANAGER (ID: " + managerRole.getId() + ")");
            } else {
                System.out.println("❌ Failed to create employee user: " + empResult.getMessage());
            }
            
            scanner.close();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connectionPool != null) {
                connectionPool.close();
            }
        }
    }
}