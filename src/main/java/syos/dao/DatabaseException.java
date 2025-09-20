package syos.dao;

/**
 * Custom exception class for database-related operations
 */
public class DatabaseException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private final String errorCode;
    private final String operation;

    public DatabaseException(String message) {
        super(message);
        this.errorCode = null;
        this.operation = null;
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.operation = null;
    }

    public DatabaseException(String message, String errorCode, String operation) {
        super(message);
        this.errorCode = errorCode;
        this.operation = operation;
    }

    public DatabaseException(String message, Throwable cause, String errorCode, String operation) {
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
        StringBuilder sb = new StringBuilder();
        sb.append("DatabaseException{");
        if (errorCode != null) {
            sb.append("errorCode='").append(errorCode).append("', ");
        }
        if (operation != null) {
            sb.append("operation='").append(operation).append("', ");
        }
        sb.append("message='").append(getMessage()).append("'");
        sb.append("}");
        return sb.toString();
    }
}