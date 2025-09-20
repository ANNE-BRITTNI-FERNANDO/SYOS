package syos.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import syos.dao.DAOFactory;
import syos.dao.DatabaseException;
import syos.models.Role;
import syos.models.User;

/**
 * Service for user management operations
 * Handles user CRUD operations, authentication, and role management
 */
public class UserService extends AbstractService {
    
    public UserService(DAOFactory daoFactory) {
        super(daoFactory);
    }
    
    @Override
    protected void doInitialize() throws ServiceException {
        // Service-specific initialization if needed
    }
    
    @Override
    protected void doCleanup() throws ServiceException {
        // Service-specific cleanup if needed
    }
    
    @Override
    public String getServiceName() {
        return "UserService";
    }
    
    /**
     * Create a new user
     * @param user The user to create
     * @return The created user with ID
     * @throws ServiceException if creation fails
     */
    public User createUser(User user, String plainPassword) throws ServiceException {
        validateInitialized();
        validateNotNull(user, "user");
        validateNotEmpty(user.getUsername(), "username");
        validateNotEmpty(user.getEmail(), "email");
        validateNotEmpty(plainPassword, "password");
        
        try {
            // Check if username already exists
            if (isUsernameExists(user.getUsername())) {
                throw new ServiceException("Username already exists: " + user.getUsername(), 
                                         "USERNAME_EXISTS", "CREATE_USER");
            }
            
            // Check if email already exists
            if (isEmailExists(user.getEmail())) {
                throw new ServiceException("Email already exists: " + user.getEmail(), 
                                         "EMAIL_EXISTS", "CREATE_USER");
            }
            
            // Hash the password
            String hashedPassword = hashPassword(plainPassword);
            user.setPasswordHash(hashedPassword);
            
            // Create the user
            User createdUser = daoFactory.getUserDAO().create(user);
            
            return createdUser;
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to create user: " + e.getMessage(), 
                                     "DATABASE_ERROR", "CREATE_USER", e);
        }
    }
    
    /**
     * Update an existing user
     * @param user The user to update
     * @return The updated user
     * @throws ServiceException if update fails
     */
    public User updateUser(User user, String plainPassword) throws ServiceException {
        validateInitialized();
        validateNotNull(user, "user");
        validatePositive(user.getId(), "user ID");
        
        try {
            // Check if user exists
            Optional<User> existingUser = getUserById(user.getId());
            if (!existingUser.isPresent()) {
                throw new ServiceException("User not found with ID: " + user.getId(), 
                                         "USER_NOT_FOUND", "UPDATE_USER");
            }
            
            // If password is being updated, hash it
            if (plainPassword != null && !plainPassword.isEmpty()) {
                String hashedPassword = hashPassword(plainPassword);
                user.setPasswordHash(hashedPassword);
            }
            
            // Update the user
            User updatedUser = daoFactory.getUserDAO().update(user);
            
            return updatedUser;
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to update user: " + e.getMessage(), 
                                     "DATABASE_ERROR", "UPDATE_USER", e);
        }
    }
    
    /**
     * Get user by ID
     * @param userId The user ID
     * @return Optional containing the user if found
     * @throws ServiceException if retrieval fails
     */
    public Optional<User> getUserById(Integer userId) throws ServiceException {
        validateInitialized();
        validatePositive(userId, "userId");
        
        try {
            Optional<User> userOpt = daoFactory.getUserDAO().findById(userId);
            return userOpt;
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to get user by ID: " + e.getMessage(), 
                                     "DATABASE_ERROR", "GET_USER_BY_ID", e);
        }
    }
    
    /**
     * Get user by username
     * @param username The username
     * @return Optional containing the user if found
     * @throws ServiceException if retrieval fails
     */
    public Optional<User> getUserByUsername(String username) throws ServiceException {
        validateInitialized();
        validateNotEmpty(username, "username");
        
        try {
            return daoFactory.getUserDAO().findByUsername(username);
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to get user by username: " + e.getMessage(), 
                                     "DATABASE_ERROR", "GET_USER_BY_USERNAME", e);
        }
    }
    
    /**
     * Get user by email
     * @param email The email
     * @return Optional containing the user if found
     * @throws ServiceException if retrieval fails
     */
    public Optional<User> getUserByEmail(String email) throws ServiceException {
        validateInitialized();
        validateNotEmpty(email, "email");
        
        try {
            return daoFactory.getUserDAO().findByEmail(email);
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to get user by email: " + e.getMessage(), 
                                     "DATABASE_ERROR", "GET_USER_BY_EMAIL", e);
        }
    }
    
    /**
     * Get all users
     * @return List of all users
     * @throws ServiceException if retrieval fails
     */
    public List<User> getAllUsers() throws ServiceException {
        validateInitialized();
        
        try {
            return daoFactory.getUserDAO().findAll();
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to get all users: " + e.getMessage(), 
                                     "DATABASE_ERROR", "GET_ALL_USERS", e);
        }
    }
    
    /**
     * Get active users
     * @return List of active users
     * @throws ServiceException if retrieval fails
     */
    public List<User> getActiveUsers() throws ServiceException {
        validateInitialized();
        
        try {
            return daoFactory.getUserDAO().findActiveUsers();
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to get active users: " + e.getMessage(), 
                                     "DATABASE_ERROR", "GET_ACTIVE_USERS", e);
        }
    }
    
    /**
     * Deactivate a user
     * @param userId The user ID to deactivate
     * @throws ServiceException if deactivation fails
     */
    public void deactivateUser(Integer userId) throws ServiceException {
        validateInitialized();
        validatePositive(userId, "userId");
        
        try {
            Optional<User> userOpt = getUserById(userId);
            if (!userOpt.isPresent()) {
                throw new ServiceException("User not found with ID: " + userId, 
                                         "USER_NOT_FOUND", "DEACTIVATE_USER");
            }
            
            User user = userOpt.get();
            user.setActive(false);
            daoFactory.getUserDAO().update(user);
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to deactivate user: " + e.getMessage(), 
                                     "DATABASE_ERROR", "DEACTIVATE_USER", e);
        }
    }
    
    /**
     * Activate a user
     * @param userId The user ID to activate
     * @throws ServiceException if activation fails
     */
    public void activateUser(Integer userId) throws ServiceException {
        validateInitialized();
        validatePositive(userId, "userId");
        
        try {
            Optional<User> userOpt = getUserById(userId);
            if (!userOpt.isPresent()) {
                throw new ServiceException("User not found with ID: " + userId, 
                                         "USER_NOT_FOUND", "ACTIVATE_USER");
            }
            
            User user = userOpt.get();
            user.setActive(true);
            daoFactory.getUserDAO().update(user);
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to activate user: " + e.getMessage(), 
                                     "DATABASE_ERROR", "ACTIVATE_USER", e);
        }
    }
    
    /**
     * Change user password
     * @param userId The user ID
     * @param oldPassword The current password
     * @param newPassword The new password
     * @throws ServiceException if password change fails
     */
    public void changePassword(Integer userId, String oldPassword, String newPassword) throws ServiceException {
        validateInitialized();
        validatePositive(userId, "userId");
        validateNotEmpty(oldPassword, "oldPassword");
        validateNotEmpty(newPassword, "newPassword");
        
        try {
            Optional<User> userOpt = getUserById(userId);
            if (!userOpt.isPresent()) {
                throw new ServiceException("User not found with ID: " + userId, 
                                         "USER_NOT_FOUND", "CHANGE_PASSWORD");
            }
            
            User user = userOpt.get();
            
            // Verify old password
            if (!verifyPassword(oldPassword, user.getPasswordHash())) {
                throw new ServiceException("Invalid current password", 
                                         "INVALID_PASSWORD", "CHANGE_PASSWORD");
            }
            
            // Hash and set new password
            String hashedNewPassword = hashPassword(newPassword);
            user.setPasswordHash(hashedNewPassword);
            
            daoFactory.getUserDAO().update(user);
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to change password: " + e.getMessage(), 
                                     "DATABASE_ERROR", "CHANGE_PASSWORD", e);
        }
    }
    
    /**
     * Assign role to user
     * @param userId The user ID
     * @param roleId The role ID
     * @throws ServiceException if role assignment fails
     */
    public void assignRole(Integer userId, Integer roleId) throws ServiceException {
        validateInitialized();
        validatePositive(userId, "userId");
        validatePositive(roleId, "roleId");
        
        try {
            // Check if user exists
            Optional<User> userOpt = getUserById(userId);
            if (!userOpt.isPresent()) {
                throw new ServiceException("User not found with ID: " + userId, 
                                         "USER_NOT_FOUND", "ASSIGN_ROLE");
            }
            
            // Check if role exists
            Optional<Role> roleOpt = daoFactory.getRoleDAO().findById(roleId);
            if (!roleOpt.isPresent()) {
                throw new ServiceException("Role not found with ID: " + roleId, 
                                         "ROLE_NOT_FOUND", "ASSIGN_ROLE");
            }
            
            // Update user's role
            User user = userOpt.get();
            user.setRoleId(roleId);
            daoFactory.getUserDAO().update(user);
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to assign role: " + e.getMessage(), 
                                     "DATABASE_ERROR", "ASSIGN_ROLE", e);
        }
    }
    
    /**
     * Remove role from user
     * @param userId The user ID
     * @param roleId The role ID
     * @throws ServiceException if role removal fails
     */
    public void removeRole(Integer userId) throws ServiceException {
        validateInitialized();
        validatePositive(userId, "userId");
        
        try {
            // Check if user exists
            Optional<User> userOpt = getUserById(userId);
            if (!userOpt.isPresent()) {
                throw new ServiceException("User not found with ID: " + userId, 
                                         "USER_NOT_FOUND", "REMOVE_ROLE");
            }
            
            // Remove role from user by setting roleId to null
            User user = userOpt.get();
            user.setRoleId(null);
            daoFactory.getUserDAO().update(user);
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to remove role: " + e.getMessage(), 
                                     "DATABASE_ERROR", "REMOVE_ROLE", e);
        }
    }
    
    /**
     * Check if username exists
     * @param username The username to check
     * @return true if username exists, false otherwise
     * @throws ServiceException if check fails
     */
    public boolean isUsernameExists(String username) throws ServiceException {
        validateInitialized();
        validateNotEmpty(username, "username");
        
        try {
            Optional<User> userOpt = daoFactory.getUserDAO().findByUsername(username);
            return userOpt.isPresent();
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to check username existence: " + e.getMessage(), 
                                     "DATABASE_ERROR", "CHECK_USERNAME", e);
        }
    }
    
    /**
     * Check if email exists
     * @param email The email to check
     * @return true if email exists, false otherwise
     * @throws ServiceException if check fails
     */
    public boolean isEmailExists(String email) throws ServiceException {
        validateInitialized();
        validateNotEmpty(email, "email");
        
        try {
            Optional<User> userOpt = daoFactory.getUserDAO().findByEmail(email);
            return userOpt.isPresent();
            
        } catch (DatabaseException e) {
            throw new ServiceException("Failed to check email existence: " + e.getMessage(), 
                                     "DATABASE_ERROR", "CHECK_EMAIL", e);
        }
    }
    
    // Private helper methods for password hashing
    
    /**
     * Hash a password using SHA-256 with salt
     * @param password The plain text password
     * @return The hashed password with salt
     * @throws ServiceException if hashing fails
     */
    private String hashPassword(String password) throws ServiceException {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Hash the password with salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combine salt and hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("Failed to hash password: " + e.getMessage(), 
                                     "HASHING_ERROR", "HASH_PASSWORD", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * @param password The plain text password
     * @param storedHash The stored hash
     * @return true if password matches, false otherwise
     * @throws ServiceException if verification fails
     */
    private boolean verifyPassword(String password, String storedHash) throws ServiceException {
        try {
            // Decode the stored hash
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            // Extract salt (first 16 bytes)
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, 16);
            
            // Extract hash (remaining bytes)
            byte[] storedPasswordHash = new byte[combined.length - 16];
            System.arraycopy(combined, 16, storedPasswordHash, 0, storedPasswordHash.length);
            
            // Hash the provided password with the extracted salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] providedPasswordHash = md.digest(password.getBytes());
            
            // Compare the hashes
            return MessageDigest.isEqual(storedPasswordHash, providedPasswordHash);
            
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("Failed to verify password: " + e.getMessage(), 
                                     "VERIFICATION_ERROR", "VERIFY_PASSWORD", e);
        } catch (IllegalArgumentException e) {
            // Invalid Base64 string
            return false;
        }
    }
}