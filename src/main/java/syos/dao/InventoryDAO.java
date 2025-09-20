package syos.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.Inventory;

/**
 * DAO implementation for Inventory entity
 */
public class InventoryDAO extends AbstractDAO<Inventory, Integer> {
    
    public InventoryDAO(ConnectionPool connectionPool) {
        super(connectionPool, "inventory");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO inventory (product_id, batch_id, quantity_on_hand, " +
               "quantity_reserved, quantity_available, reorder_level, max_stock_level, " +
               "location, last_stock_check, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, product_id, batch_id, quantity_on_hand, quantity_reserved, " +
               "quantity_available, reorder_level, max_stock_level, location, " +
               "last_stock_check, created_at, updated_at FROM inventory";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE inventory SET product_id = ?, batch_id = ?, quantity_on_hand = ?, " +
               "quantity_reserved = ?, quantity_available = ?, reorder_level = ?, " +
               "max_stock_level = ?, location = ?, last_stock_check = ?, " +
               "updated_at = NOW() WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM inventory WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, Inventory inventory) throws SQLException {
        statement.setObject(1, inventory.getProductId());
        statement.setObject(2, inventory.getBatchId());
        statement.setInt(3, inventory.getQuantityOnHand());
        statement.setInt(4, inventory.getQuantityReserved());
        statement.setInt(5, inventory.getQuantityAvailable());
        statement.setObject(6, inventory.getReorderLevel());
        statement.setObject(7, inventory.getMaxStockLevel());
        statement.setString(8, inventory.getLocation());
        
        if (inventory.getLastStockCheck() != null) {
            statement.setTimestamp(9, Timestamp.valueOf(inventory.getLastStockCheck()));
        } else {
            statement.setTimestamp(9, null);
        }
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, Inventory inventory) throws SQLException {
        statement.setObject(1, inventory.getProductId());
        statement.setObject(2, inventory.getBatchId());
        statement.setInt(3, inventory.getQuantityOnHand());
        statement.setInt(4, inventory.getQuantityReserved());
        statement.setInt(5, inventory.getQuantityAvailable());
        statement.setObject(6, inventory.getReorderLevel());
        statement.setObject(7, inventory.getMaxStockLevel());
        statement.setString(8, inventory.getLocation());
        
        if (inventory.getLastStockCheck() != null) {
            statement.setTimestamp(9, Timestamp.valueOf(inventory.getLastStockCheck()));
        } else {
            statement.setTimestamp(9, null);
        }
        
        statement.setInt(10, inventory.getId());
    }
    
    @Override
    protected Inventory mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setId(resultSet.getInt("id"));
        
        Integer productId = resultSet.getObject("product_id", Integer.class);
        inventory.setProductId(productId);
        
        Integer batchId = resultSet.getObject("batch_id", Integer.class);
        inventory.setBatchId(batchId);
        
        inventory.setQuantityOnHand(resultSet.getInt("quantity_on_hand"));
        inventory.setQuantityReserved(resultSet.getInt("quantity_reserved"));
        inventory.setQuantityAvailable(resultSet.getInt("quantity_available"));
        
        Integer reorderLevel = resultSet.getObject("reorder_level", Integer.class);
        inventory.setReorderLevel(reorderLevel);
        
        Integer maxStockLevel = resultSet.getObject("max_stock_level", Integer.class);
        inventory.setMaxStockLevel(maxStockLevel);
        
        inventory.setLocation(resultSet.getString("location"));
        
        Timestamp lastStockCheck = resultSet.getTimestamp("last_stock_check");
        if (lastStockCheck != null) {
            inventory.setLastStockCheck(lastStockCheck.toLocalDateTime());
        }
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            inventory.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            inventory.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return inventory;
    }
    
    @Override
    protected Integer getEntityId(Inventory inventory) {
        return inventory.getId();
    }
    
    @Override
    protected void setEntityId(Inventory inventory, Integer id) {
        inventory.setId(id);
    }
    
    // Custom query methods
    
    /**
     * Find inventory by product ID
     * @param productId The product ID to search for
     * @return Optional containing the inventory if found
     * @throws DatabaseException if query fails
     */
    public Optional<Inventory> findByProductId(Integer productId) throws DatabaseException {
        if (productId == null) {
            return Optional.empty();
        }
        
        String sql = getSelectAllSQL() + " WHERE product_id = ?";
        return executeQueryForSingleResult(sql, productId);
    }
    
    /**
     * Find inventory items that need reordering
     * @return List of inventory items below reorder level
     * @throws DatabaseException if query fails
     */
    public List<Inventory> findItemsNeedingReorder() throws DatabaseException {
        String sql = getSelectAllSQL() + " WHERE reorder_level IS NOT NULL AND quantity_on_hand <= reorder_level";
        return executeQuery(sql);
    }
    
    /**
     * Find inventory items that are out of stock
     * @return List of inventory items with zero quantity
     * @throws DatabaseException if query fails
     */
    public List<Inventory> findOutOfStockItems() throws DatabaseException {
        String sql = getSelectAllSQL() + " WHERE quantity_on_hand <= 0";
        return executeQuery(sql);
    }
    
    /**
     * Find inventory items by location
     * @param location The location to search for
     * @return List of inventory items at the specified location
     * @throws DatabaseException if query fails
     */
    public List<Inventory> findByLocation(String location) throws DatabaseException {
        if (location == null || location.trim().isEmpty()) {
            return List.of();
        }
        
        String sql = getSelectAllSQL() + " WHERE location = ?";
        return executeQuery(sql, location);
    }
}