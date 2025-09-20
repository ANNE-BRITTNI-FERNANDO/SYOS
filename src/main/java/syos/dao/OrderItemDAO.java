package syos.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.OrderItem;

/**
 * DAO implementation for OrderItem entity
 */
public class OrderItemDAO extends AbstractDAO<OrderItem, Integer> {
    
    public OrderItemDAO(ConnectionPool connectionPool) {
        super(connectionPool, "order_items");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO order_items (order_id, product_id, batch_id, quantity, " +
               "unit_price, subtotal, tax_rate, tax_amount, discount_amount, " +
               "created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, order_id, product_id, batch_id, quantity, unit_price, " +
               "subtotal, tax_rate, tax_amount, discount_amount, created_at, updated_at " +
               "FROM order_items";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE order_items SET order_id = ?, product_id = ?, batch_id = ?, " +
               "quantity = ?, unit_price = ?, subtotal = ?, tax_rate = ?, " +
               "tax_amount = ?, discount_amount = ?, updated_at = NOW() WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM order_items WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, OrderItem orderItem) throws SQLException {
        statement.setObject(1, orderItem.getOrderId());
        statement.setObject(2, orderItem.getProductId());
        statement.setObject(3, orderItem.getBatchId());
        statement.setInt(4, orderItem.getQuantity());
        statement.setBigDecimal(5, orderItem.getUnitPrice());
        statement.setBigDecimal(6, orderItem.getSubtotal());
        statement.setBigDecimal(7, orderItem.getTaxRate());
        statement.setBigDecimal(8, orderItem.getTaxAmount());
        statement.setBigDecimal(9, orderItem.getDiscountAmount());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, OrderItem orderItem) throws SQLException {
        statement.setObject(1, orderItem.getOrderId());
        statement.setObject(2, orderItem.getProductId());
        statement.setObject(3, orderItem.getBatchId());
        statement.setInt(4, orderItem.getQuantity());
        statement.setBigDecimal(5, orderItem.getUnitPrice());
        statement.setBigDecimal(6, orderItem.getSubtotal());
        statement.setBigDecimal(7, orderItem.getTaxRate());
        statement.setBigDecimal(8, orderItem.getTaxAmount());
        statement.setBigDecimal(9, orderItem.getDiscountAmount());
        statement.setInt(10, orderItem.getId());
    }
    
    @Override
    protected OrderItem mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(resultSet.getInt("id"));
        
        Integer orderId = resultSet.getObject("order_id", Integer.class);
        orderItem.setOrderId(orderId);
        
        Integer productId = resultSet.getObject("product_id", Integer.class);
        orderItem.setProductId(productId);
        
        Integer batchId = resultSet.getObject("batch_id", Integer.class);
        orderItem.setBatchId(batchId);
        
        orderItem.setQuantity(resultSet.getInt("quantity"));
        orderItem.setUnitPrice(resultSet.getBigDecimal("unit_price"));
        orderItem.setSubtotal(resultSet.getBigDecimal("subtotal"));
        orderItem.setTaxRate(resultSet.getBigDecimal("tax_rate"));
        orderItem.setTaxAmount(resultSet.getBigDecimal("tax_amount"));
        orderItem.setDiscountAmount(resultSet.getBigDecimal("discount_amount"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            orderItem.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            orderItem.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return orderItem;
    }
    
    @Override
    protected Integer getEntityId(OrderItem orderItem) {
        return orderItem.getId();
    }
    
    @Override
    protected void setEntityId(OrderItem orderItem, Integer id) {
        orderItem.setId(id);
    }
    
    // Custom query methods
    
    /**
     * Find order items by order ID
     * @param orderId The order ID to search for
     * @return List of order items for the specified order
     * @throws DatabaseException if query fails
     */
    public List<OrderItem> findByOrderId(Integer orderId) throws DatabaseException {
        if (orderId == null) {
            return List.of();
        }
        
        String sql = getSelectAllSQL() + " WHERE order_id = ?";
        return executeQuery(sql, orderId);
    }
    
    /**
     * Find order items by product ID
     * @param productId The product ID to search for
     * @return List of order items containing the specified product
     * @throws DatabaseException if query fails
     */
    public List<OrderItem> findByProductId(Integer productId) throws DatabaseException {
        if (productId == null) {
            return List.of();
        }
        
        String sql = getSelectAllSQL() + " WHERE product_id = ?";
        return executeQuery(sql, productId);
    }
    
    /**
     * Find order items by batch ID
     * @param batchId The batch ID to search for
     * @return List of order items from the specified batch
     * @throws DatabaseException if query fails
     */
    public List<OrderItem> findByBatchId(Integer batchId) throws DatabaseException {
        if (batchId == null) {
            return List.of();
        }
        
        String sql = getSelectAllSQL() + " WHERE batch_id = ?";
        return executeQuery(sql, batchId);
    }
    
    /**
     * Delete all order items for a specific order
     * @param orderId The order ID
     * @return Number of deleted order items
     * @throws DatabaseException if deletion fails
     */
    public int deleteByOrderId(Integer orderId) throws DatabaseException {
        if (orderId == null) {
            return 0;
        }
        
        String sql = "DELETE FROM order_items WHERE order_id = ?";
        return executeUpdate(sql, orderId);
    }
    
    /**
     * Calculate total quantity sold for a product
     * @param productId The product ID
     * @return Total quantity sold
     * @throws DatabaseException if query fails
     */
    public int getTotalQuantitySoldForProduct(Integer productId) throws DatabaseException {
        if (productId == null) {
            return 0;
        }
        
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM order_items oi " +
                    "INNER JOIN orders o ON oi.order_id = o.id " +
                    "WHERE oi.product_id = ? AND o.status IN ('DELIVERED', 'CONFIRMED')";
        
        try (var connection = connectionPool.getConnection();
             var statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, productId);
            
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get total quantity sold: " + e.getMessage(), e, e.getSQLState(), "TOTAL_QUANTITY_SOLD");
        }
    }
    
    /**
     * Calculate total revenue for a product
     * @param productId The product ID
     * @return Total revenue from the product
     * @throws DatabaseException if query fails
     */
    public BigDecimal getTotalRevenueForProduct(Integer productId) throws DatabaseException {
        if (productId == null) {
            return BigDecimal.ZERO;
        }
        
        String sql = "SELECT COALESCE(SUM(oi.subtotal + oi.tax_amount), 0) FROM order_items oi " +
                    "INNER JOIN orders o ON oi.order_id = o.id " +
                    "WHERE oi.product_id = ? AND o.status IN ('DELIVERED', 'CONFIRMED')";
        
        try (var connection = connectionPool.getConnection();
             var statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, productId);
            
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getBigDecimal(1) : BigDecimal.ZERO;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get total revenue: " + e.getMessage(), e, e.getSQLState(), "TOTAL_REVENUE");
        }
    }
    
    /**
     * Get top selling products
     * @param limit Maximum number of products to return
     * @return List of Object arrays containing [productId, totalQuantitySold, totalRevenue]
     * @throws DatabaseException if query fails
     */
    public List<Object[]> getTopSellingProducts(int limit) throws DatabaseException {
        if (limit <= 0) {
            return List.of();
        }
        
        String sql = "SELECT oi.product_id, SUM(oi.quantity) as total_quantity, " +
                    "SUM(oi.subtotal + oi.tax_amount) as total_revenue " +
                    "FROM order_items oi " +
                    "INNER JOIN orders o ON oi.order_id = o.id " +
                    "WHERE o.status IN ('DELIVERED', 'CONFIRMED') " +
                    "GROUP BY oi.product_id " +
                    "ORDER BY total_quantity DESC " +
                    "LIMIT ?";
        
        try (var connection = connectionPool.getConnection();
             var statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, limit);
            
            try (var resultSet = statement.executeQuery()) {
                List<Object[]> results = new java.util.ArrayList<>();
                while (resultSet.next()) {
                    results.add(new Object[] {
                        resultSet.getInt("product_id"),
                        resultSet.getInt("total_quantity"),
                        resultSet.getBigDecimal("total_revenue")
                    });
                }
                return results;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get top selling products: " + e.getMessage(), e, e.getSQLState(), "TOP_SELLING_PRODUCTS");
        }
    }
}