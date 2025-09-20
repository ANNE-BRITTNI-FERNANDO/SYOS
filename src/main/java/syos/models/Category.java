package syos.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Category model representing product categories
 * Supports hierarchical category structure
 */
public class Category {
    private Integer id;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Integer parentCategoryId;
    private Category parentCategory;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Category() {}

    public Category(String categoryCode, String categoryName, String description) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.description = description;
        this.isActive = true;
    }

    public Category(Integer id, String categoryCode, String categoryName, String description,
                   Integer parentCategoryId, boolean isActive, LocalDateTime createdAt, 
                   LocalDateTime updatedAt) {
        this.id = id;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.description = description;
        this.parentCategoryId = parentCategoryId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        if (categoryCode == null || categoryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Category code cannot be null or empty");
        }
        this.categoryCode = categoryCode.trim().toUpperCase();
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        this.categoryName = categoryName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
        if (parentCategory != null) {
            this.parentCategoryId = parentCategory.getId();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business methods
    public boolean isRootCategory() {
        return parentCategoryId == null;
    }

    public boolean isSubCategory() {
        return parentCategoryId != null;
    }

    public boolean isValidCategory() {
        return categoryCode != null && !categoryCode.trim().isEmpty() &&
               categoryName != null && !categoryName.trim().isEmpty();
    }

    public String getFullCategoryPath() {
        if (parentCategory != null) {
            return parentCategory.getFullCategoryPath() + " > " + categoryName;
        }
        return categoryName;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(categoryCode, category.categoryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categoryCode);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", categoryCode='" + categoryCode + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", description='" + description + '\'' +
                ", parentCategoryId=" + parentCategoryId +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}