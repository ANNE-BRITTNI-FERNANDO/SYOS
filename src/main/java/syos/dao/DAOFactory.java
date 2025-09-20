package syos.dao;

import com.syos.infrastructure.database.ConnectionPool;

/**
 * Factory class for creating and managing DAO instances
 * Implements Singleton pattern and provides centralized DAO management
 */
public class DAOFactory {
    private static DAOFactory instance;
    private final ConnectionPool connectionPool;
    
    // DAO instances
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private PermissionDAO permissionDAO;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private BatchDAO batchDAO;
    private CustomerDAO customerDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private TransactionDAO transactionDAO;
    private InventoryDAO inventoryDAO;
    private AuditLogDAO auditLogDAO;
    
    private DAOFactory(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    
    /**
     * Get singleton instance of DAOFactory
     * @param connectionPool The connection pool to use
     * @return DAOFactory instance
     */
    public static synchronized DAOFactory getInstance(ConnectionPool connectionPool) {
        if (instance == null) {
            instance = new DAOFactory(connectionPool);
        }
        return instance;
    }
    
    /**
     * Get singleton instance (requires previous initialization)
     * @return DAOFactory instance
     * @throws IllegalStateException if not previously initialized
     */
    public static DAOFactory getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DAOFactory must be initialized with ConnectionPool first");
        }
        return instance;
    }
    
    // User DAO
    public UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new UserDAO(connectionPool);
        }
        return userDAO;
    }
    
    // Role DAO
    public RoleDAO getRoleDAO() {
        if (roleDAO == null) {
            roleDAO = new RoleDAO(connectionPool);
        }
        return roleDAO;
    }
    
    // Permission DAO
    public PermissionDAO getPermissionDAO() {
        if (permissionDAO == null) {
            permissionDAO = new PermissionDAO(connectionPool);
        }
        return permissionDAO;
    }
    
    // Product DAO
    public ProductDAO getProductDAO() {
        if (productDAO == null) {
            productDAO = new ProductDAO(connectionPool);
        }
        return productDAO;
    }
    
    // Category DAO
    public CategoryDAO getCategoryDAO() {
        if (categoryDAO == null) {
            categoryDAO = new CategoryDAO(connectionPool);
        }
        return categoryDAO;
    }
    
    // Batch DAO
    public BatchDAO getBatchDAO() {
        if (batchDAO == null) {
            batchDAO = new BatchDAO(connectionPool);
        }
        return batchDAO;
    }
    
    // Customer DAO
    public CustomerDAO getCustomerDAO() {
        if (customerDAO == null) {
            customerDAO = new CustomerDAO(connectionPool);
        }
        return customerDAO;
    }
    
    // Order DAO
    public OrderDAO getOrderDAO() {
        if (orderDAO == null) {
            orderDAO = new OrderDAO(connectionPool);
        }
        return orderDAO;
    }
    
    // OrderItem DAO
    public OrderItemDAO getOrderItemDAO() {
        if (orderItemDAO == null) {
            orderItemDAO = new OrderItemDAO(connectionPool);
        }
        return orderItemDAO;
    }
    
    // Transaction DAO
    public TransactionDAO getTransactionDAO() {
        if (transactionDAO == null) {
            transactionDAO = new TransactionDAO(connectionPool);
        }
        return transactionDAO;
    }
    
    // Inventory DAO
    public InventoryDAO getInventoryDAO() {
        if (inventoryDAO == null) {
            inventoryDAO = new InventoryDAO(connectionPool);
        }
        return inventoryDAO;
    }
    
    // AuditLog DAO
    public AuditLogDAO getAuditLogDAO() {
        if (auditLogDAO == null) {
            auditLogDAO = new AuditLogDAO(connectionPool);
        }
        return auditLogDAO;
    }
    
    /**
     * Close all DAO resources (if needed in the future)
     */
    public void close() {
        // Reset all DAO instances
        userDAO = null;
        roleDAO = null;
        permissionDAO = null;
        productDAO = null;
        categoryDAO = null;
        batchDAO = null;
        customerDAO = null;
        orderDAO = null;
        orderItemDAO = null;
        transactionDAO = null;
        inventoryDAO = null;
        auditLogDAO = null;
    }
    
    /**
     * Reset factory instance (for testing purposes)
     */
    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }
}