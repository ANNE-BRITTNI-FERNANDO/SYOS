package syos.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Permission model representing system permissions
 * Used for role-based access control (RBAC)
 */
public class Permission {
    private Integer id;
    private String permissionName;
    private String description;
    private String module;
    private LocalDateTime createdAt;

    // Constructors
    public Permission() {}

    public Permission(String permissionName, String description, String module) {
        this.permissionName = permissionName;
        this.description = description;
        this.module = module;
    }

    public Permission(Integer id, String permissionName, String description, 
                     String module, LocalDateTime createdAt) {
        this.id = id;
        this.permissionName = permissionName;
        this.description = description;
        this.module = module;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Permission name cannot be null or empty");
        }
        this.permissionName = permissionName.trim().toUpperCase();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        if (module == null || module.trim().isEmpty()) {
            throw new IllegalArgumentException("Module cannot be null or empty");
        }
        this.module = module.trim().toUpperCase();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Business methods
    public boolean isValidPermission() {
        return permissionName != null && !permissionName.trim().isEmpty() &&
               module != null && !module.trim().isEmpty();
    }

    public boolean belongsToModule(String moduleName) {
        return module != null && module.equalsIgnoreCase(moduleName);
    }

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id) && Objects.equals(permissionName, that.permissionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, permissionName);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", permissionName='" + permissionName + '\'' +
                ", description='" + description + '\'' +
                ", module='" + module + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}