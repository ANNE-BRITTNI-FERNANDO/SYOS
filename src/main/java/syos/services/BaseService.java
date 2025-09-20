package syos.services;

/**
 * Base interface for all services
 * Provides common service operations and lifecycle management
 */
public interface BaseService {
    
    /**
     * Initialize the service
     * @throws ServiceException if initialization fails
     */
    void initialize() throws ServiceException;
    
    /**
     * Check if the service is initialized and ready
     * @return true if service is ready, false otherwise
     */
    boolean isInitialized();
    
    /**
     * Cleanup service resources
     * @throws ServiceException if cleanup fails
     */
    void cleanup() throws ServiceException;
    
    /**
     * Get the service name
     * @return service name
     */
    String getServiceName();
}