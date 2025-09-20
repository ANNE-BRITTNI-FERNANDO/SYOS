package syos.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.Role;

/**
 * DAO implementation for Role entity
 */
public class RoleDAO extends AbstractDAO<Role, Integer> {
    
    public RoleDAO(ConnectionPool connectionPool) {
        super(connectionPool, "roles");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO roles (role_name, description, created_at, updated_at) " +
               "VALUES (?, ?, NOW(), NOW())";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, role_name, description, created_at, updated_at FROM roles";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE roles SET role_name = ?, description = ?, updated_at = NOW() WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM roles WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, Role role) throws SQLException {
        statement.setString(1, role.getRoleName());
        statement.setString(2, role.getDescription());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, Role role) throws SQLException {
        statement.setString(1, role.getRoleName());
        statement.setString(2, role.getDescription());
        statement.setInt(3, role.getId());
    }
    
    @Override
    protected Role mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Role role = new Role();
        role.setId(resultSet.getInt("id"));
        role.setRoleName(resultSet.getString("role_name"));
        role.setDescription(resultSet.getString("description"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            role.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            role.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return role;
    }
    
    @Override
    protected Integer getEntityId(Role role) {
        return role.getId();
    }
    
    @Override
    protected void setEntityId(Role role, Integer id) {
        role.setId(id);
    }
    
    /**
     * Find role by name
     * @param roleName The role name to search for
     * @return Optional containing the role if found
     * @throws DatabaseException if query fails
     */
    public Optional<Role> findByRoleName(String roleName) throws DatabaseException {
        if (roleName == null || roleName.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = getSelectAllSQL() + " WHERE role_name = ?";
        return executeQueryForSingleResult(sql, roleName.trim().toUpperCase());
    }
    
    /**
     * Find role by name (alias for findByRoleName)
     * @param name The role name to search for
     * @return Role if found, null otherwise
     * @throws DatabaseException if query fails
     */
    public Role findByName(String name) throws DatabaseException {
        return findByRoleName(name).orElse(null);
    }
}