package syos.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.Batch;

/**
 * DAO implementation for Batch entity (stub)
 */
public class BatchDAO extends AbstractDAO<Batch, Integer> {
    
    public BatchDAO(ConnectionPool connectionPool) {
        super(connectionPool, "batches");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO batches (batch_number, product_id, quantity, cost_price) VALUES (?, ?, ?, ?)";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, batch_number, product_id, quantity, cost_price FROM batches";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE batches SET batch_number = ?, product_id = ?, quantity = ?, cost_price = ? WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM batches WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, Batch batch) throws SQLException {
        statement.setString(1, batch.getBatchNumber());
        statement.setObject(2, batch.getProductId());
        statement.setInt(3, batch.getQuantity());
        statement.setBigDecimal(4, batch.getCostPrice());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, Batch batch) throws SQLException {
        statement.setString(1, batch.getBatchNumber());
        statement.setObject(2, batch.getProductId());
        statement.setInt(3, batch.getQuantity());
        statement.setBigDecimal(4, batch.getCostPrice());
        statement.setInt(5, batch.getId());
    }
    
    @Override
    protected Batch mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Batch batch = new Batch();
        batch.setId(resultSet.getInt("id"));
        batch.setBatchNumber(resultSet.getString("batch_number"));
        batch.setProductId(resultSet.getInt("product_id"));
        batch.setQuantity(resultSet.getInt("quantity"));
        batch.setCostPrice(resultSet.getBigDecimal("cost_price"));
        return batch;
    }
    
    @Override
    protected Integer getEntityId(Batch batch) {
        return batch.getId();
    }
    
    @Override
    protected void setEntityId(Batch batch, Integer id) {
        batch.setId(id);
    }
}