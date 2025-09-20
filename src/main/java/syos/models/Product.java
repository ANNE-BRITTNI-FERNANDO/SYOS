package syos.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Product model representing products in the store
 */
public class Product {
    private Integer id;
    private String productCode;
    private String productName;
    private String description;
    private Integer categoryId;
    private Category category;
    private String brand;
    private String manufacturer;
    private BigDecimal unitPrice;
    private Integer reorderLevel;
    private String barcode;
    private String unitOfMeasure;
    private boolean isActive;
    private boolean isTaxable;
    private BigDecimal taxRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Product() {}

    public Product(String productCode, String productName, Integer categoryId, BigDecimal unitPrice) {
        this.productCode = productCode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.unitPrice = unitPrice;
        this.isActive = true;
        this.isTaxable = false;
        this.taxRate = BigDecimal.ZERO;
        this.unitOfMeasure = "UNIT";
    }

    public Product(Integer id, String productCode, String productName, String description,
                  Integer categoryId, String brand, String manufacturer, BigDecimal unitPrice,
                  Integer reorderLevel, String barcode, String unitOfMeasure, boolean isActive,
                  boolean isTaxable, BigDecimal taxRate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.productCode = productCode;
        this.productName = productName;
        this.description = description;
        this.categoryId = categoryId;
        this.brand = brand;
        this.manufacturer = manufacturer;
        this.unitPrice = unitPrice;
        this.reorderLevel = reorderLevel;
        this.barcode = barcode;
        this.unitOfMeasure = unitOfMeasure;
        this.isActive = isActive;
        this.isTaxable = isTaxable;
        this.taxRate = taxRate;
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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be null or empty");
        }
        this.productCode = productCode.trim().toUpperCase();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        this.productName = productName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("Category ID must be a positive integer");
        }
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be null or negative");
        }
        this.unitPrice = unitPrice;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        if (reorderLevel != null && reorderLevel < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative");
        }
        this.reorderLevel = reorderLevel;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        if (unitOfMeasure == null || unitOfMeasure.trim().isEmpty()) {
            this.unitOfMeasure = "UNIT";
        } else {
            this.unitOfMeasure = unitOfMeasure.trim().toUpperCase();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isTaxable() {
        return isTaxable;
    }

    public void setTaxable(boolean taxable) {
        isTaxable = taxable;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        if (taxRate == null) {
            this.taxRate = BigDecimal.ZERO;
        } else if (taxRate.compareTo(BigDecimal.ZERO) < 0 || taxRate.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 100");
        } else {
            this.taxRate = taxRate;
        }
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
    public boolean isValidProduct() {
        return productCode != null && !productCode.trim().isEmpty() &&
               productName != null && !productName.trim().isEmpty() &&
               categoryId != null && categoryId > 0 &&
               unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) >= 0;
    }

    public BigDecimal calculateTaxAmount() {
        if (isTaxable && taxRate != null) {
            return unitPrice.multiply(taxRate).divide(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.add(calculateTaxAmount());
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void enableTax(BigDecimal taxRate) {
        this.isTaxable = true;
        setTaxRate(taxRate);
    }

    public void disableTax() {
        this.isTaxable = false;
        this.taxRate = BigDecimal.ZERO;
    }

    public boolean requiresReorder(int currentStock) {
        return reorderLevel != null && currentStock <= reorderLevel;
    }

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(productCode, product.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productCode);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", categoryId=" + categoryId +
                ", brand='" + brand + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", unitPrice=" + unitPrice +
                ", reorderLevel=" + reorderLevel +
                ", barcode='" + barcode + '\'' +
                ", unitOfMeasure='" + unitOfMeasure + '\'' +
                ", isActive=" + isActive +
                ", isTaxable=" + isTaxable +
                ", taxRate=" + taxRate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}