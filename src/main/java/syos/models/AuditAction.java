package syos.models;

/**
 * Enumeration of audit actions for logging user activities
 */
public enum AuditAction {
    // User Management Actions
    USER_REGISTRATION_SUCCESS("User Registration Success"),
    USER_REGISTRATION_FAILED("User Registration Failed"),
    USER_LOGIN_SUCCESS("User Login Success"),
    USER_LOGIN_FAILED("User Login Failed"),
    USER_LOGOUT("User Logout"),
    USER_PASSWORD_CHANGED("User Password Changed"),
    USER_PASSWORD_CHANGE_FAILED("User Password Change Failed"),
    USER_PROFILE_UPDATED("User Profile Updated"),
    USER_ACTIVATED("User Account Activated"),
    USER_DEACTIVATED("User Account Deactivated"),
    USER_SESSION_EXPIRED("User Session Expired"),
    
    // Product Management Actions
    PRODUCT_CREATED("Product Created"),
    PRODUCT_UPDATED("Product Updated"),
    PRODUCT_DELETED("Product Deleted"),
    PRODUCT_VIEWED("Product Viewed"),
    
    // Inventory Management Actions
    INVENTORY_UPDATED("Inventory Updated"),
    INVENTORY_ADJUSTMENT("Inventory Adjustment"),
    STOCK_LEVEL_CHANGED("Stock Level Changed"),
    
    // Order Management Actions
    ORDER_CREATED("Order Created"),
    ORDER_UPDATED("Order Updated"),
    ORDER_CANCELLED("Order Cancelled"),
    ORDER_FULFILLED("Order Fulfilled"),
    ORDER_PAYMENT_PROCESSED("Order Payment Processed"),
    
    // System Actions
    SYSTEM_BACKUP_CREATED("System Backup Created"),
    SYSTEM_MAINTENANCE("System Maintenance"),
    DATA_EXPORT("Data Export"),
    DATA_IMPORT("Data Import"),
    
    // Security Actions
    UNAUTHORIZED_ACCESS_ATTEMPT("Unauthorized Access Attempt"),
    PERMISSION_DENIED("Permission Denied"),
    SECURITY_POLICY_VIOLATION("Security Policy Violation");
    
    private final String description;
    
    AuditAction(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}