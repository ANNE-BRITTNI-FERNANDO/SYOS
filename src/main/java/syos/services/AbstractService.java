package syos.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import syos.dao.DAOFactory;

/**
 * Abstract base class for all service implementations
 * Provides common functionality and manages DAO access
 */
public abstract class AbstractService implements BaseService {
    protected final DAOFactory daoFactory;
    protected final Logger logger;
    protected boolean initialized = false;
    
    protected AbstractService(DAOFactory daoFactory) {
        if (daoFactory == null) {
            throw new IllegalArgumentException("DAOFactory cannot be null");
        }
        this.daoFactory = daoFactory;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
    
    @Override
    public void initialize() throws ServiceException {
        if (initialized) {
            return;
        }
        
        try {
            doInitialize();
            initialized = true;
        } catch (Exception e) {
            throw new ServiceException("Failed to initialize service: " + getServiceName(), 
                                     "INITIALIZATION_FAILED", "INITIALIZE", e);
        }
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public void cleanup() throws ServiceException {
        if (!initialized) {
            return;
        }
        
        try {
            doCleanup();
            initialized = false;
        } catch (Exception e) {
            throw new ServiceException("Failed to cleanup service: " + getServiceName(), 
                                     "CLEANUP_FAILED", "CLEANUP", e);
        }
    }
    
    /**
     * Template method for service-specific initialization
     * @throws ServiceException if initialization fails
     */
    protected abstract void doInitialize() throws ServiceException;
    
    /**
     * Template method for service-specific cleanup
     * @throws ServiceException if cleanup fails
     */
    protected abstract void doCleanup() throws ServiceException;
    
    /**
     * Validate that the service is initialized before performing operations
     * @throws ServiceException if service is not initialized
     */
    protected void validateInitialized() throws ServiceException {
        if (!initialized) {
            throw new ServiceException("Service not initialized: " + getServiceName(), 
                                     "SERVICE_NOT_INITIALIZED", "VALIDATE");
        }
    }
    
    /**
     * Validate input parameters
     * @param parameter The parameter to validate
     * @param parameterName The name of the parameter for error messages
     * @throws ServiceException if parameter is null
     */
    protected void validateNotNull(Object parameter, String parameterName) throws ServiceException {
        if (parameter == null) {
            throw new ServiceException("Parameter cannot be null: " + parameterName, 
                                     "NULL_PARAMETER", "VALIDATE");
        }
    }
    
    /**
     * Validate string parameters
     * @param parameter The string parameter to validate
     * @param parameterName The name of the parameter for error messages
     * @throws ServiceException if parameter is null or empty
     */
    protected void validateNotEmpty(String parameter, String parameterName) throws ServiceException {
        if (parameter == null || parameter.trim().isEmpty()) {
            throw new ServiceException("Parameter cannot be null or empty: " + parameterName, 
                                     "EMPTY_PARAMETER", "VALIDATE");
        }
    }
    
    /**
     * Validate positive integer parameters
     * @param parameter The integer parameter to validate
     * @param parameterName The name of the parameter for error messages
     * @throws ServiceException if parameter is null or not positive
     */
    protected void validatePositive(Integer parameter, String parameterName) throws ServiceException {
        validateNotNull(parameter, parameterName);
        if (parameter <= 0) {
            throw new ServiceException("Parameter must be positive: " + parameterName, 
                                     "INVALID_PARAMETER", "VALIDATE");
        }
    }
}