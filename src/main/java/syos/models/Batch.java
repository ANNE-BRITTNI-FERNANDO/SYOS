package syos.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Batch model representing product batches for inventory tracking
 */
public class Batch {
    private Integer id;
    private String batchNumber;
    private Integer productId;
    private Product product;
    private Integer quantity;
    private BigDecimal costPrice;
    private LocalDate expiryDate;
    private String supplier;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Batch() {}

    public Batch(String batchNumber, Integer productId, Integer quantity, BigDecimal costPrice) {
        this.batchNumber = batchNumber;
        this.productId = productId;
        this.quantity = quantity;
        this.costPrice = costPrice;
        this.isActive = true;
    }

    public Batch(Integer id, String batchNumber, Integer productId, Integer quantity, 
                BigDecimal costPrice, LocalDate expiryDate, String supplier, boolean isActive,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.batchNumber = batchNumber;
        this.productId = productId;
        this.quantity = quantity;
        this.costPrice = costPrice;
        this.expiryDate = expiryDate;
        this.supplier = supplier;
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

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        if (batchNumber == null || batchNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Batch number cannot be null or empty");
        }
        this.batchNumber = batchNumber.trim().toUpperCase();
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product ID must be a positive integer");
        }
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
        }
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be null or negative");
        }
        this.quantity = quantity;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        if (costPrice == null || costPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cost price cannot be null or negative");
        }
        this.costPrice = costPrice;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiry date cannot be in the past");
        }
        this.expiryDate = expiryDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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
    public boolean isValidBatch() {
        return batchNumber != null && !batchNumber.trim().isEmpty() &&
               productId != null && productId > 0 &&
               quantity != null && quantity >= 0 &&
               costPrice != null && costPrice.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringWithinDays(int days) {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now().plusDays(days));
    }

    public boolean hasStock() {
        return quantity != null && quantity > 0;
    }

    public boolean canWithdraw(int requestedQuantity) {
        return hasStock() && quantity >= requestedQuantity && isActive && !isExpired();
    }

    public void withdrawQuantity(int withdrawalQuantity) {
        if (withdrawalQuantity <= 0) {
            throw new IllegalArgumentException("Withdrawal quantity must be positive");
        }
        if (!canWithdraw(withdrawalQuantity)) {
            throw new IllegalStateException("Cannot withdraw " + withdrawalQuantity + " items from batch");
        }
        this.quantity -= withdrawalQuantity;
    }

    public void addQuantity(int additionalQuantity) {
        if (additionalQuantity <= 0) {
            throw new IllegalArgumentException("Additional quantity must be positive");
        }
        this.quantity += additionalQuantity;
    }

    public BigDecimal getTotalValue() {
        return costPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public int getDaysUntilExpiry() {
        if (expiryDate == null) return Integer.MAX_VALUE;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Batch batch = (Batch) o;
        return Objects.equals(id, batch.id) && Objects.equals(batchNumber, batch.batchNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, batchNumber);
    }

    @Override
    public String toString() {
        return "Batch{" +
                "id=" + id +
                ", batchNumber='" + batchNumber + '\'' +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", costPrice=" + costPrice +
                ", expiryDate=" + expiryDate +
                ", supplier='" + supplier + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}