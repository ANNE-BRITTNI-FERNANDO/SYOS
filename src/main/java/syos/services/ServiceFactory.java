package syos.services;

import syos.dao.DAOFactory;

/**
 * Factory class for creating and managing service instances
 * Implements Singleton pattern and provides centralized service management
 */
public class ServiceFactory {
    private static ServiceFactory instance;
    private final DAOFactory daoFactory;
    
    // Service instances
    private UserService userService;
    private AuthenticationService authenticationService;
    // TODO: Implement remaining services
    // private ProductService productService;
    // private OrderService orderService;
    // private InventoryService inventoryService;
    // private ReportService reportService;
    // private AuditService auditService;
    
    private ServiceFactory(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    /**
     * Get singleton instance of ServiceFactory
     * @param daoFactory The DAO factory to use
     * @return ServiceFactory instance
     */
    public static synchronized ServiceFactory getInstance(DAOFactory daoFactory) {
        if (instance == null) {
            instance = new ServiceFactory(daoFactory);
        }
        return instance;
    }
    
    /**
     * Get singleton instance (requires previous initialization)
     * @return ServiceFactory instance
     * @throws IllegalStateException if not previously initialized
     */
    public static ServiceFactory getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ServiceFactory must be initialized with DAOFactory first");
        }
        return instance;
    }
    
    // User Service
    public UserService getUserService() throws ServiceException {
        if (userService == null) {
            userService = new UserService(daoFactory);
            userService.initialize();
        }
        return userService;
    }
    
    // Authentication Service
    public AuthenticationService getAuthenticationService() throws ServiceException {
        if (authenticationService == null) {
            authenticationService = new AuthenticationService(daoFactory);
            authenticationService.initialize();
        }
        return authenticationService;
    }
    
    // TODO: Implement remaining service getters
    /*
    // Product Service
    public ProductService getProductService() throws ServiceException {
        if (productService == null) {
            productService = new ProductService(daoFactory);
            productService.initialize();
        }
        return productService;
    }
    
    // Order Service
    public OrderService getOrderService() throws ServiceException {
        if (orderService == null) {
            orderService = new OrderService(daoFactory);
            orderService.initialize();
        }
        return orderService;
    }
    
    // Inventory Service
    public InventoryService getInventoryService() throws ServiceException {
        if (inventoryService == null) {
            inventoryService = new InventoryService(daoFactory);
            inventoryService.initialize();
        }
        return inventoryService;
    }
    
    // Authentication Service
    public AuthenticationService getAuthenticationService() throws ServiceException {
        if (authenticationService == null) {
            authenticationService = new AuthenticationService(daoFactory);
            authenticationService.initialize();
        }
        return authenticationService;
    }
    
    // Report Service
    public ReportService getReportService() throws ServiceException {
        if (reportService == null) {
            reportService = new ReportService(daoFactory);
            reportService.initialize();
        }
        return reportService;
    }
    
    // Audit Service
    public AuditService getAuditService() throws ServiceException {
        if (auditService == null) {
            auditService = new AuditService(daoFactory);
            auditService.initialize();
        }
        return auditService;
    }
    */
    
    /**
     * Initialize all services
     * @throws ServiceException if any service fails to initialize
     */
    public void initializeAllServices() throws ServiceException {
        getUserService();
        getAuthenticationService();
        // TODO: Initialize remaining services when implemented
        /*
        getProductService();
        getOrderService();
        getInventoryService();
        getReportService();
        getAuditService();
        */
    }
    
    /**
     * Cleanup all service resources
     * @throws ServiceException if cleanup fails
     */
    public void cleanup() throws ServiceException {
        ServiceException lastException = null;
        
        // Cleanup UserService
        if (userService != null) {
            try {
                userService.cleanup();
            } catch (ServiceException e) {
                lastException = e;
            }
            userService = null;
        }
        
        // Cleanup AuthenticationService
        if (authenticationService != null) {
            try {
                authenticationService.cleanup();
            } catch (ServiceException e) {
                lastException = e;
            }
            authenticationService = null;
        }
        
        // TODO: Cleanup remaining services when implemented
        /*
        if (productService != null) {
            try {
                productService.cleanup();
            } catch (ServiceException e) {
                lastException = e;
            }
            productService = null;
        }
        
        if (orderService != null) {
            try {
                orderService.cleanup();
            } catch (ServiceException e) {
                lastException = e;
            }
            orderService = null;
        }
        
        if (inventoryService != null) {
            try {
                inventoryService.cleanup();
            } catch (ServiceException e) {
                lastException = e;
            }
            inventoryService = null;
        }
        
        if (authenticationService != null) {
            try {
                authenticationService.cleanup();
            } catch (ServiceException e) {
                lastException = e;
            }
            authenticationService = null;
        }
        
        if (reportService != null) {
            try {
                reportService.cleanup();
            } catch (ServiceException e) {
                lastException = e;
            }
            reportService = null;
        }
        
        if (auditService != null) {
            try {
                auditService.cleanup();
            } catch (ServiceException e) {
                lastException = e;
            }
            auditService = null;
        }
        */
        
        if (lastException != null) {
            throw lastException;
        }
    }
    
    /**
     * Reset factory instance (for testing purposes)
     */
    public static synchronized void resetInstance() {
        if (instance != null) {
            try {
                instance.cleanup();
            } catch (ServiceException e) {
                // Log error but continue reset
                System.err.println("Error during factory cleanup: " + e.getMessage());
            }
            instance = null;
        }
    }
}