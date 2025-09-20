package syos.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.Permission;

/**
 * DAO implementation for Permission entity (stub)
 */
public class PermissionDAO extends AbstractDAO<Permission, Integer> {
    
    public PermissionDAO(ConnectionPool connectionPool) {
        super(connectionPool, "permissions");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO permissions (permission_name, description) VALUES (?, ?)";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, permission_name, description FROM permissions";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE permissions SET permission_name = ?, description = ? WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM permissions WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, Permission permission) throws SQLException {
        statement.setString(1, permission.getPermissionName());
        statement.setString(2, permission.getDescription());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, Permission permission) throws SQLException {
        statement.setString(1, permission.getPermissionName());
        statement.setString(2, permission.getDescription());
        statement.setInt(3, permission.getId());
    }
    
    @Override
    protected Permission mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Permission permission = new Permission();
        permission.setId(resultSet.getInt("id"));
        permission.setPermissionName(resultSet.getString("permission_name"));
        permission.setDescription(resultSet.getString("description"));
        return permission;
    }
    
    @Override
    protected Integer getEntityId(Permission permission) {
        return permission.getId();
    }
    
    @Override
    protected void setEntityId(Permission permission, Integer id) {
        permission.setId(id);
    }
}