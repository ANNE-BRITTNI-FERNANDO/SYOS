package syos.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.Order;
import syos.models.OrderItem;

/**
 * DAO implementation for Order entity
 */
public class OrderDAO extends AbstractDAO<Order, Integer> {
    
    private final OrderItemDAO orderItemDAO;
    
    public OrderDAO(ConnectionPool connectionPool) {
        super(connectionPool, "orders");
        this.orderItemDAO = new OrderItemDAO(connectionPool);
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO orders (order_number, customer_id, status, subtotal, " +
               "tax_amount, discount_amount, total_amount, user_id, notes, " +
               "order_date, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, order_number, customer_id, status, subtotal, tax_amount, " +
               "discount_amount, total_amount, user_id, notes, order_date, " +
               "created_at, updated_at FROM orders";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE orders SET order_number = ?, customer_id = ?, status = ?, " +
               "subtotal = ?, tax_amount = ?, discount_amount = ?, total_amount = ?, " +
               "user_id = ?, notes = ?, order_date = ?, updated_at = NOW() WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM orders WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, Order order) throws SQLException {
        statement.setString(1, order.getOrderNumber());
        statement.setObject(2, order.getCustomerId());
        statement.setString(3, order.getStatus().name());
        statement.setBigDecimal(4, order.getSubtotal());
        statement.setBigDecimal(5, order.getTaxAmount());
        statement.setBigDecimal(6, order.getDiscountAmount());
        statement.setBigDecimal(7, order.getTotalAmount());
        statement.setObject(8, order.getUserId());
        statement.setString(9, order.getNotes());
        
        if (order.getOrderDate() != null) {
            statement.setTimestamp(10, Timestamp.valueOf(order.getOrderDate()));
        } else {
            statement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
        }
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, Order order) throws SQLException {
        statement.setString(1, order.getOrderNumber());
        statement.setObject(2, order.getCustomerId());
        statement.setString(3, order.getStatus().name());
        statement.setBigDecimal(4, order.getSubtotal());
        statement.setBigDecimal(5, order.getTaxAmount());
        statement.setBigDecimal(6, order.getDiscountAmount());
        statement.setBigDecimal(7, order.getTotalAmount());
        statement.setObject(8, order.getUserId());
        statement.setString(9, order.getNotes());
        
        if (order.getOrderDate() != null) {
            statement.setTimestamp(10, Timestamp.valueOf(order.getOrderDate()));
        } else {
            statement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
        }
        
        statement.setInt(11, order.getId());
    }
    
    @Override
    protected Order mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getInt("id"));
        order.setOrderNumber(resultSet.getString("order_number"));
        
        Integer customerId = resultSet.getObject("customer_id", Integer.class);
        order.setCustomerId(customerId);
        
        String statusStr = resultSet.getString("status");
        if (statusStr != null) {
            order.setStatus(Order.OrderStatus.valueOf(statusStr));
        }
        
        order.setSubtotal(resultSet.getBigDecimal("subtotal"));
        order.setTaxAmount(resultSet.getBigDecimal("tax_amount"));
        order.setDiscountAmount(resultSet.getBigDecimal("discount_amount"));
        order.setTotalAmount(resultSet.getBigDecimal("total_amount"));
        
        Integer userId = resultSet.getObject("user_id", Integer.class);
        order.setUserId(userId);
        
        order.setNotes(resultSet.getString("notes"));
        
        Timestamp orderDate = resultSet.getTimestamp("order_date");
        if (orderDate != null) {
            order.setOrderDate(orderDate.toLocalDateTime());
        }
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            order.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return order;
    }
    
    @Override
    protected Integer getEntityId(Order order) {
        return order.getId();
    }
    
    @Override
    protected void setEntityId(Order order, Integer id) {
        order.setId(id);
    }
    
    /**
     * Find order with its order items
     * @param id The order ID
     * @return Optional containing the order with items if found
     * @throws DatabaseException if query fails
     */
    public Optional<Order> findByIdWithItems(Integer id) throws DatabaseException {
        Optional<Order> orderOpt = findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());
            order.setOrderItems(items);
            return Optional.of(order);
        }
        return Optional.empty();
    }
    
    /**
     * Create order with order items
     * @param order The order to create
     * @return The created order with items
     * @throws DatabaseException if creation fails
     */
    public Order createWithItems(Order order) throws DatabaseException {
        if (order == null) {
            throw new DatabaseException("Order cannot be null", "NULL_ORDER", "CREATE_WITH_ITEMS");
        }
        
        // Create the order first
        Order createdOrder = create(order);
        
        // Create order items if any
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems != null && !orderItems.isEmpty()) {
            for (OrderItem item : orderItems) {
                item.setOrderId(createdOrder.getId());
                orderItemDAO.create(item);
            }
            createdOrder.setOrderItems(orderItems);
        }
        
        return createdOrder;
    }
    
    /**
     * Find order by order number
     * @param orderNumber The order number to search for
     * @return Optional containing the order if found
     * @throws DatabaseException if query fails
     */
    public Optional<Order> findByOrderNumber(String orderNumber) throws DatabaseException {
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = getSelectAllSQL() + " WHERE order_number = ?";
        return executeQueryForSingleResult(sql, orderNumber.trim().toUpperCase());
    }
    
    /**
     * Find orders by customer ID
     * @param customerId The customer ID to search for
     * @return List of orders for the specified customer
     * @throws DatabaseException if query fails
     */
    public List<Order> findByCustomerId(Integer customerId) throws DatabaseException {
        if (customerId == null) {
            return List.of();
        }
        
        String sql = getSelectAllSQL() + " WHERE customer_id = ? ORDER BY order_date DESC";
        return executeQuery(sql, customerId);
    }
    
    /**
     * Find orders by status
     * @param status The order status to search for
     * @return List of orders with the specified status
     * @throws DatabaseException if query fails
     */
    public List<Order> findByStatus(Order.OrderStatus status) throws DatabaseException {
        if (status == null) {
            return List.of();
        }
        
        String sql = getSelectAllSQL() + " WHERE status = ? ORDER BY order_date DESC";
        return executeQuery(sql, status.name());
    }
    
    /**
     * Find orders by user ID (who created the order)
     * @param userId The user ID to search for
     * @return List of orders created by the specified user
     * @throws DatabaseException if query fails
     */
    public List<Order> findByUserId(Integer userId) throws DatabaseException {
        if (userId == null) {
            return List.of();
        }
        
        String sql = getSelectAllSQL() + " WHERE user_id = ? ORDER BY order_date DESC";
        return executeQuery(sql, userId);
    }
    
    /**
     * Find orders within date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of orders within the specified date range
     * @throws DatabaseException if query fails
     */
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws DatabaseException {
        if (startDate == null && endDate == null) {
            return findAll();
        }
        
        StringBuilder sql = new StringBuilder(getSelectAllSQL() + " WHERE ");
        if (startDate != null && endDate != null) {
            sql.append("order_date BETWEEN ? AND ? ORDER BY order_date DESC");
            return executeQuery(sql.toString(), Timestamp.valueOf(startDate), Timestamp.valueOf(endDate));
        } else if (startDate != null) {
            sql.append("order_date >= ? ORDER BY order_date DESC");
            return executeQuery(sql.toString(), Timestamp.valueOf(startDate));
        } else {
            sql.append("order_date <= ? ORDER BY order_date DESC");
            return executeQuery(sql.toString(), Timestamp.valueOf(endDate));
        }
    }
    
    /**
     * Find orders within total amount range
     * @param minAmount Minimum total amount (inclusive)
     * @param maxAmount Maximum total amount (inclusive)
     * @return List of orders within the specified total amount range
     * @throws DatabaseException if query fails
     */
    public List<Order> findByTotalAmountRange(BigDecimal minAmount, BigDecimal maxAmount) throws DatabaseException {
        if (minAmount == null && maxAmount == null) {
            return findAll();
        }
        
        StringBuilder sql = new StringBuilder(getSelectAllSQL() + " WHERE ");
        if (minAmount != null && maxAmount != null) {
            sql.append("total_amount BETWEEN ? AND ? ORDER BY order_date DESC");
            return executeQuery(sql.toString(), minAmount, maxAmount);
        } else if (minAmount != null) {
            sql.append("total_amount >= ? ORDER BY order_date DESC");
            return executeQuery(sql.toString(), minAmount);
        } else {
            sql.append("total_amount <= ? ORDER BY order_date DESC");
            return executeQuery(sql.toString(), maxAmount);
        }
    }
    
    /**
     * Check if order number already exists
     * @param orderNumber The order number to check
     * @return true if order number exists, false otherwise
     * @throws DatabaseException if query fails
     */
    public boolean orderNumberExists(String orderNumber) throws DatabaseException {
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM orders WHERE order_number = ?";
        
        try (var connection = connectionPool.getConnection();
             var statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, orderNumber.trim().toUpperCase());
            
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check order number existence: " + e.getMessage(), e, e.getSQLState(), "CHECK_ORDER_NUMBER");
        }
    }
    
    /**
     * Update order status
     * @param orderId The order ID
     * @param newStatus The new status
     * @return true if status was updated, false if order not found
     * @throws DatabaseException if update fails
     */
    public boolean updateStatus(Integer orderId, Order.OrderStatus newStatus) throws DatabaseException {
        if (orderId == null || newStatus == null) {
            return false;
        }
        
        String sql = "UPDATE orders SET status = ?, updated_at = NOW() WHERE id = ?";
        return executeUpdate(sql, newStatus.name(), orderId) > 0;
    }
    
    /**
     * Calculate total sales amount for a date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Total sales amount
     * @throws DatabaseException if query fails
     */
    public BigDecimal calculateTotalSales(LocalDateTime startDate, LocalDateTime endDate) throws DatabaseException {
        StringBuilder sql = new StringBuilder("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status IN ('DELIVERED', 'CONFIRMED')");
        
        if (startDate != null && endDate != null) {
            sql.append(" AND order_date BETWEEN ? AND ?");
            
            try (var connection = connectionPool.getConnection();
                 var statement = connection.prepareStatement(sql.toString())) {
                
                statement.setTimestamp(1, Timestamp.valueOf(startDate));
                statement.setTimestamp(2, Timestamp.valueOf(endDate));
                
                try (var resultSet = statement.executeQuery()) {
                    return resultSet.next() ? resultSet.getBigDecimal(1) : BigDecimal.ZERO;
                }
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to calculate total sales: " + e.getMessage(), e, e.getSQLState(), "CALCULATE_SALES");
            }
        } else {
            try (var connection = connectionPool.getConnection();
                 var statement = connection.prepareStatement(sql.toString());
                 var resultSet = statement.executeQuery()) {
                
                return resultSet.next() ? resultSet.getBigDecimal(1) : BigDecimal.ZERO;
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to calculate total sales: " + e.getMessage(), e, e.getSQLState(), "CALCULATE_SALES");
            }
        }
    }
    
    /**
     * Get order statistics for a date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Array containing [totalOrders, totalSales, averageOrderValue]
     * @throws DatabaseException if query fails
     */
    public Object[] getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) throws DatabaseException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*), COALESCE(SUM(total_amount), 0), COALESCE(AVG(total_amount), 0) FROM orders WHERE 1=1");
        
        if (startDate != null) {
            sql.append(" AND order_date >= ?");
        }
        if (endDate != null) {
            sql.append(" AND order_date <= ?");
        }
        
        try (var connection = connectionPool.getConnection();
             var statement = connection.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (startDate != null) {
                statement.setTimestamp(paramIndex++, Timestamp.valueOf(startDate));
            }
            if (endDate != null) {
                statement.setTimestamp(paramIndex, Timestamp.valueOf(endDate));
            }
            
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Object[] {
                        resultSet.getLong(1),      // totalOrders
                        resultSet.getBigDecimal(2), // totalSales
                        resultSet.getBigDecimal(3)  // averageOrderValue
                    };
                }
                return new Object[] { 0L, BigDecimal.ZERO, BigDecimal.ZERO };
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get order statistics: " + e.getMessage(), e, e.getSQLState(), "ORDER_STATISTICS");
        }
    }
}