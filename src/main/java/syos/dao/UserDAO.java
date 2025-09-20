package syos.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.User;

/**
 * DAO implementation for User entity
 */
public class UserDAO extends AbstractDAO<User, Integer> {
    
    public UserDAO(ConnectionPool connectionPool) {
        super(connectionPool, "users");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO users (username, password_hash, salt, email, first_name, last_name, " +
               "phone_number, role_id, is_active, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, username, password_hash, salt, email, first_name, last_name, " +
               "phone_number, role_id, is_active, created_at, updated_at FROM users";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE users SET username = ?, password_hash = ?, salt = ?, email = ?, " +
               "first_name = ?, last_name = ?, phone_number = ?, role_id = ?, " +
               "is_active = ?, updated_at = NOW() WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM users WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPasswordHash());
        statement.setString(3, user.getSalt());
        statement.setString(4, user.getEmail());
        statement.setString(5, user.getFirstName());
        statement.setString(6, user.getLastName());
        statement.setString(7, user.getPhone());
        statement.setObject(8, user.getRoleId());
        statement.setBoolean(9, user.isActive());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPasswordHash());
        statement.setString(3, user.getSalt());
        statement.setString(4, user.getEmail());
        statement.setString(5, user.getFirstName());
        statement.setString(6, user.getLastName());
        statement.setString(7, user.getPhone());
        statement.setObject(8, user.getRoleId());
        statement.setBoolean(9, user.isActive());
        statement.setInt(10, user.getId());
    }
    
    @Override
    protected User mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setSalt(resultSet.getString("salt"));
        user.setEmail(resultSet.getString("email"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setPhone(resultSet.getString("phone_number"));
        
        Integer roleId = resultSet.getObject("role_id", Integer.class);
        user.setRoleId(roleId);
        
        user.setActive(resultSet.getBoolean("is_active"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
    
    @Override
    protected Integer getEntityId(User user) {
        return user.getId();
    }
    
    @Override
    protected void setEntityId(User user, Integer id) {
        user.setId(id);
    }
    
    // Custom query methods
    
    /**
     * Find user by username
     * @param username The username to search for
     * @return Optional containing the user if found
     * @throws DatabaseException if query fails
     */
    public Optional<User> findByUsername(String username) throws DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = getSelectAllSQL() + " WHERE username = ?";
        return executeQueryForSingleResult(sql, username);
    }
    
    /**
     * Find user by email
     * @param email The email to search for
     * @return Optional containing the user if found
     * @throws DatabaseException if query fails
     */
    public Optional<User> findByEmail(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = getSelectAllSQL() + " WHERE email = ?";
        return executeQueryForSingleResult(sql, email);
    }
    
    /**
     * Find all active users
     * @return List of active users
     * @throws DatabaseException if query fails
     */
    public List<User> findActiveUsers() throws DatabaseException {
        String sql = getSelectAllSQL() + " WHERE is_active = true";
        return executeQuery(sql);
    }
    
    /**
     * Find users by role ID
     * @param roleId The role ID to search for
     * @return List of users with the specified role
     * @throws DatabaseException if query fails
     */
    public List<User> findByRoleId(Integer roleId) throws DatabaseException {
        if (roleId == null) {
            return List.of();
        }
        
        String sql = getSelectAllSQL() + " WHERE role_id = ?";
        return executeQuery(sql, roleId);
    }
    
    /**
     * Check if username already exists
     * @param username The username to check
     * @return true if username exists, false otherwise
     * @throws DatabaseException if query fails
     */
    public boolean usernameExists(String username) throws DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (var connection = connectionPool.getConnection();
             var statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, username);
            
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check username existence: " + e.getMessage(), e, e.getSQLState(), "CHECK_USERNAME");
        }
    }
    
    /**
     * Check if email already exists
     * @param email The email to check
     * @return true if email exists, false otherwise
     * @throws DatabaseException if query fails
     */
    public boolean emailExists(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (var connection = connectionPool.getConnection();
             var statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email);
            
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check email existence: " + e.getMessage(), e, e.getSQLState(), "CHECK_EMAIL");
        }
    }
    
    /**
     * Activate a user account
     * @param userId The user ID to activate
     * @return true if user was activated, false if not found
     * @throws DatabaseException if update fails
     */
    public boolean activateUser(Integer userId) throws DatabaseException {
        if (userId == null) {
            return false;
        }
        
        String sql = "UPDATE users SET is_active = true, updated_at = NOW() WHERE id = ?";
        return executeUpdate(sql, userId) > 0;
    }
    
    /**
     * Deactivate a user account
     * @param userId The user ID to deactivate
     * @return true if user was deactivated, false if not found
     * @throws DatabaseException if update fails
     */
    public boolean deactivateUser(Integer userId) throws DatabaseException {
        if (userId == null) {
            return false;
        }
        
        String sql = "UPDATE users SET is_active = false, updated_at = NOW() WHERE id = ?";
        return executeUpdate(sql, userId) > 0;
    }
    
    /**
     * Update user password
     * @param userId The user ID
     * @param newPasswordHash The new password hash
     * @return true if password was updated, false if user not found
     * @throws DatabaseException if update fails
     */
    public boolean updatePassword(Integer userId, String newPasswordHash) throws DatabaseException {
        if (userId == null || newPasswordHash == null || newPasswordHash.trim().isEmpty()) {
            return false;
        }
        
        String sql = "UPDATE users SET password_hash = ?, updated_at = NOW() WHERE id = ?";
        return executeUpdate(sql, newPasswordHash, userId) > 0;
    }
    
    /**
     * Search users by name (first name or last name)
     * @param searchTerm The search term
     * @return List of users matching the search
     * @throws DatabaseException if query fails
     */
    public List<User> searchByName(String searchTerm) throws DatabaseException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        
        String likePattern = "%" + searchTerm.trim() + "%";
        String sql = getSelectAllSQL() + " WHERE first_name LIKE ? OR last_name LIKE ?";
        return executeQuery(sql, likePattern, likePattern);
    }
}