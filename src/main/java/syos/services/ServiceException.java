package syos.services;

/**
 * Custom exception for service layer operations
 */
public class ServiceException extends Exception {
    private final String errorCode;
    private final String operation;
    
    public ServiceException(String message) {
        super(message);
        this.errorCode = "UNKNOWN_ERROR";
        this.operation = "UNKNOWN";
    }
    
    public ServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.operation = "UNKNOWN";
    }
    
    public ServiceException(String message, String errorCode, String operation) {
        super(message);
        this.errorCode = errorCode;
        this.operation = operation;
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN_ERROR";
        this.operation = "UNKNOWN";
    }
    
    public ServiceException(String message, String errorCode, String operation, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.operation = operation;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getOperation() {
        return operation;
    }
    
    @Override
    public String toString() {
        return "ServiceException{" +
                "message='" + getMessage() + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", operation='" + operation + '\'' +
                '}';
    }
}