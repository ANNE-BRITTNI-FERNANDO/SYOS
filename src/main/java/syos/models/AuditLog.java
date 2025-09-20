package syos.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * AuditLog model for tracking system changes and user actions
 */
public class AuditLog {
    private Integer id;
    private String tableName;
    private String operation;
    private Integer recordId;
    private String oldValues;
    private String newValues;
    private Integer userId;
    private User user;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;

    // Enums
    public enum Operation {
        INSERT, UPDATE, DELETE, LOGIN, LOGOUT, VIEW, EXPORT, IMPORT
    }

    // Constructors
    public AuditLog() {}

    public AuditLog(String tableName, Operation operation, Integer recordId, Integer userId) {
        this.tableName = tableName;
        this.operation = operation.name();
        this.recordId = recordId;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(String tableName, Operation operation, Integer recordId, String oldValues,
                   String newValues, Integer userId, String ipAddress, String userAgent) {
        this.tableName = tableName;
        this.operation = operation.name();
        this.recordId = recordId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(Integer id, String tableName, String operation, Integer recordId,
                   String oldValues, String newValues, Integer userId, String ipAddress,
                   String userAgent, LocalDateTime timestamp) {
        this.id = id;
        this.tableName = tableName;
        this.operation = operation;
        this.recordId = recordId;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }
        this.tableName = tableName.trim().toLowerCase();
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation cannot be null or empty");
        }
        this.operation = operation.trim().toUpperCase();
    }

    public void setOperation(Operation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Operation cannot be null");
        }
        this.operation = operation.name();
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getOldValues() {
        return oldValues;
    }

    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        if (userId != null && userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive integer");
        }
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Business methods
    public boolean isValidAuditLog() {
        return tableName != null && !tableName.trim().isEmpty() &&
               operation != null && !operation.trim().isEmpty() &&
               userId != null && userId > 0 &&
               timestamp != null;
    }

    public boolean isInsertOperation() {
        return Operation.INSERT.name().equals(operation);
    }

    public boolean isUpdateOperation() {
        return Operation.UPDATE.name().equals(operation);
    }

    public boolean isDeleteOperation() {
        return Operation.DELETE.name().equals(operation);
    }

    public boolean isLoginOperation() {
        return Operation.LOGIN.name().equals(operation);
    }

    public boolean isLogoutOperation() {
        return Operation.LOGOUT.name().equals(operation);
    }

    public boolean isViewOperation() {
        return Operation.VIEW.name().equals(operation);
    }

    public boolean isExportOperation() {
        return Operation.EXPORT.name().equals(operation);
    }

    public boolean isImportOperation() {
        return Operation.IMPORT.name().equals(operation);
    }

    public boolean hasOldValues() {
        return oldValues != null && !oldValues.trim().isEmpty();
    }

    public boolean hasNewValues() {
        return newValues != null && !newValues.trim().isEmpty();
    }

    public boolean hasValueChanges() {
        return hasOldValues() && hasNewValues() && !oldValues.equals(newValues);
    }

    public boolean isDataModificationOperation() {
        return isInsertOperation() || isUpdateOperation() || isDeleteOperation();
    }

    public boolean isSystemOperation() {
        return isLoginOperation() || isLogoutOperation();
    }

    public boolean isAccessOperation() {
        return isViewOperation() || isExportOperation() || isImportOperation();
    }

    public String getOperationDescription() {
        switch (operation) {
            case "INSERT":
                return "Created new record";
            case "UPDATE":
                return "Updated existing record";
            case "DELETE":
                return "Deleted record";
            case "LOGIN":
                return "User logged in";
            case "LOGOUT":
                return "User logged out";
            case "VIEW":
                return "Viewed record";
            case "EXPORT":
                return "Exported data";
            case "IMPORT":
                return "Imported data";
            default:
                return "Unknown operation";
        }
    }

    public String getFormattedTimestamp() {
        if (timestamp == null) return "";
        return timestamp.toString();
    }

    // Static factory methods
    public static AuditLog createInsertLog(String tableName, Integer recordId, String newValues, Integer userId) {
        AuditLog log = new AuditLog(tableName, Operation.INSERT, recordId, userId);
        log.setNewValues(newValues);
        return log;
    }

    public static AuditLog createUpdateLog(String tableName, Integer recordId, String oldValues, 
                                         String newValues, Integer userId) {
        AuditLog log = new AuditLog(tableName, Operation.UPDATE, recordId, userId);
        log.setOldValues(oldValues);
        log.setNewValues(newValues);
        return log;
    }

    public static AuditLog createDeleteLog(String tableName, Integer recordId, String oldValues, Integer userId) {
        AuditLog log = new AuditLog(tableName, Operation.DELETE, recordId, userId);
        log.setOldValues(oldValues);
        return log;
    }

    public static AuditLog createLoginLog(Integer userId, String ipAddress, String userAgent) {
        AuditLog log = new AuditLog("users", Operation.LOGIN, userId, userId);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        return log;
    }

    public static AuditLog createLogoutLog(Integer userId, String ipAddress) {
        AuditLog log = new AuditLog("users", Operation.LOGOUT, userId, userId);
        log.setIpAddress(ipAddress);
        return log;
    }

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(id, auditLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", tableName='" + tableName + '\'' +
                ", operation='" + operation + '\'' +
                ", recordId=" + recordId +
                ", oldValues='" + oldValues + '\'' +
                ", newValues='" + newValues + '\'' +
                ", userId=" + userId +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}