package syos.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.AuditLog;

/**
 * DAO implementation for AuditLog entity (stub)
 */
public class AuditLogDAO extends AbstractDAO<AuditLog, Integer> {
    
    public AuditLogDAO(ConnectionPool connectionPool) {
        super(connectionPool, "audit_logs");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO audit_logs (table_name, operation, record_id, old_values, new_values, user_id) VALUES (?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, table_name, operation, record_id, old_values, new_values, user_id, timestamp FROM audit_logs";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE audit_logs SET table_name = ?, operation = ?, record_id = ?, old_values = ?, new_values = ?, user_id = ? WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM audit_logs WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, AuditLog auditLog) throws SQLException {
        statement.setString(1, auditLog.getTableName());
        statement.setString(2, auditLog.getOperation());
        statement.setObject(3, auditLog.getRecordId());
        statement.setString(4, auditLog.getOldValues());
        statement.setString(5, auditLog.getNewValues());
        statement.setObject(6, auditLog.getUserId());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, AuditLog auditLog) throws SQLException {
        statement.setString(1, auditLog.getTableName());
        statement.setString(2, auditLog.getOperation());
        statement.setObject(3, auditLog.getRecordId());
        statement.setString(4, auditLog.getOldValues());
        statement.setString(5, auditLog.getNewValues());
        statement.setObject(6, auditLog.getUserId());
        statement.setInt(7, auditLog.getId());
    }
    
    @Override
    protected AuditLog mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(resultSet.getInt("id"));
        auditLog.setTableName(resultSet.getString("table_name"));
        auditLog.setOperation(resultSet.getString("operation"));
        auditLog.setRecordId(resultSet.getInt("record_id"));
        auditLog.setOldValues(resultSet.getString("old_values"));
        auditLog.setNewValues(resultSet.getString("new_values"));
        auditLog.setUserId(resultSet.getInt("user_id"));
        auditLog.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
        return auditLog;
    }
    
    @Override
    protected Integer getEntityId(AuditLog auditLog) {
        return auditLog.getId();
    }
    
    @Override
    protected void setEntityId(AuditLog auditLog, Integer id) {
        auditLog.setId(id);
    }
    
    /**
     * Find audit logs by table name
     */
    public List<AuditLog> findByTableName(String tableName) throws DatabaseException {
        String sql = getSelectAllSQL() + " WHERE table_name = ? ORDER BY timestamp DESC";
        return executeQuery(sql, tableName);
    }
    
    /**
     * Find audit logs by user ID
     */
    public List<AuditLog> findByUserId(Integer userId) throws DatabaseException {
        String sql = getSelectAllSQL() + " WHERE user_id = ? ORDER BY timestamp DESC";
        return executeQuery(sql, userId);
    }
    
    /**
     * Find audit logs for a specific record
     */
    public List<AuditLog> findByRecord(String tableName, String recordId) throws DatabaseException {
        String sql = getSelectAllSQL() + " WHERE table_name = ? AND record_id = ? ORDER BY timestamp DESC";
        return executeQuery(sql, tableName, recordId);
    }
}