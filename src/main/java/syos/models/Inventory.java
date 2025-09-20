package syos.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Inventory model representing current stock levels
 */
public class Inventory {
    private Integer id;
    private Integer productId;
    private Product product;
    private Integer batchId;
    private Batch batch;
    private Integer quantityOnHand;
    private Integer quantityReserved;
    private Integer quantityAvailable;
    private Integer reorderLevel;
    private Integer maxStockLevel;
    private String location;
    private LocalDateTime lastStockCheck;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Inventory() {}

    public Inventory(Integer productId, Integer batchId, Integer quantityOnHand) {
        this.productId = productId;
        this.batchId = batchId;
        this.quantityOnHand = quantityOnHand;
        this.quantityReserved = 0;
        calculateAvailableQuantity();
    }

    public Inventory(Integer id, Integer productId, Integer batchId, Integer quantityOnHand,
                    Integer quantityReserved, Integer quantityAvailable, Integer reorderLevel,
                    Integer maxStockLevel, String location, LocalDateTime lastStockCheck,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.batchId = batchId;
        this.quantityOnHand = quantityOnHand;
        this.quantityReserved = quantityReserved;
        this.quantityAvailable = quantityAvailable;
        this.reorderLevel = reorderLevel;
        this.maxStockLevel = maxStockLevel;
        this.location = location;
        this.lastStockCheck = lastStockCheck;
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
            if (product.getReorderLevel() != null) {
                this.reorderLevel = product.getReorderLevel();
            }
        }
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
        if (batch != null) {
            this.batchId = batch.getId();
        }
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        if (quantityOnHand == null || quantityOnHand < 0) {
            throw new IllegalArgumentException("Quantity on hand cannot be null or negative");
        }
        this.quantityOnHand = quantityOnHand;
        calculateAvailableQuantity();
    }

    public Integer getQuantityReserved() {
        return quantityReserved;
    }

    public void setQuantityReserved(Integer quantityReserved) {
        if (quantityReserved == null || quantityReserved < 0) {
            throw new IllegalArgumentException("Quantity reserved cannot be null or negative");
        }
        this.quantityReserved = quantityReserved;
        calculateAvailableQuantity();
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
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

    public Integer getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(Integer maxStockLevel) {
        if (maxStockLevel != null && maxStockLevel < 0) {
            throw new IllegalArgumentException("Max stock level cannot be negative");
        }
        this.maxStockLevel = maxStockLevel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getLastStockCheck() {
        return lastStockCheck;
    }

    public void setLastStockCheck(LocalDateTime lastStockCheck) {
        this.lastStockCheck = lastStockCheck;
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
    public boolean isValidInventory() {
        return productId != null && productId > 0 &&
               quantityOnHand != null && quantityOnHand >= 0 &&
               quantityReserved != null && quantityReserved >= 0;
    }

    public void calculateAvailableQuantity() {
        if (quantityOnHand != null && quantityReserved != null) {
            this.quantityAvailable = quantityOnHand - quantityReserved;
        }
    }

    public boolean hasStock() {
        return quantityOnHand != null && quantityOnHand > 0;
    }

    public boolean hasAvailableStock() {
        return quantityAvailable != null && quantityAvailable > 0;
    }

    public boolean isAvailable(int requestedQuantity) {
        return hasAvailableStock() && quantityAvailable >= requestedQuantity;
    }

    public boolean requiresReorder() {
        return reorderLevel != null && quantityOnHand != null && quantityOnHand <= reorderLevel;
    }

    public boolean isOverstocked() {
        return maxStockLevel != null && quantityOnHand != null && quantityOnHand > maxStockLevel;
    }

    public boolean isOutOfStock() {
        return quantityOnHand == null || quantityOnHand <= 0;
    }

    public boolean canReserve(int requestedQuantity) {
        return requestedQuantity > 0 && quantityAvailable != null && quantityAvailable >= requestedQuantity;
    }

    public void reserveQuantity(int reserveQuantity) {
        if (reserveQuantity <= 0) {
            throw new IllegalArgumentException("Reserve quantity must be positive");
        }
        if (!canReserve(reserveQuantity)) {
            throw new IllegalStateException("Cannot reserve " + reserveQuantity + " items");
        }
        this.quantityReserved += reserveQuantity;
        calculateAvailableQuantity();
    }

    public void releaseReservation(int releaseQuantity) {
        if (releaseQuantity <= 0) {
            throw new IllegalArgumentException("Release quantity must be positive");
        }
        if (quantityReserved < releaseQuantity) {
            throw new IllegalStateException("Cannot release more than reserved quantity");
        }
        this.quantityReserved -= releaseQuantity;
        calculateAvailableQuantity();
    }

    public void addStock(int addQuantity) {
        if (addQuantity <= 0) {
            throw new IllegalArgumentException("Add quantity must be positive");
        }
        this.quantityOnHand += addQuantity;
        calculateAvailableQuantity();
    }

    public void removeStock(int removeQuantity) {
        if (removeQuantity <= 0) {
            throw new IllegalArgumentException("Remove quantity must be positive");
        }
        if (quantityOnHand < removeQuantity) {
            throw new IllegalStateException("Cannot remove more stock than available");
        }
        this.quantityOnHand -= removeQuantity;
        calculateAvailableQuantity();
    }

    public void adjustStock(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("New quantity cannot be negative");
        }
        this.quantityOnHand = newQuantity;
        // Ensure reserved quantity doesn't exceed on-hand quantity
        if (quantityReserved != null && quantityReserved > quantityOnHand) {
            this.quantityReserved = quantityOnHand;
        }
        calculateAvailableQuantity();
    }

    public void performStockCheck() {
        this.lastStockCheck = LocalDateTime.now();
    }

    public int getStockTurnoverRate() {
        // This would typically be calculated based on sales data over time
        // For now, returning a placeholder
        return 0;
    }

    public double getStockValue() {
        if (batch != null && batch.getCostPrice() != null) {
            return batch.getCostPrice().doubleValue() * quantityOnHand;
        }
        return 0.0;
    }

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(id, inventory.id) && 
               Objects.equals(productId, inventory.productId) && 
               Objects.equals(batchId, inventory.batchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, batchId);
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", productId=" + productId +
                ", batchId=" + batchId +
                ", quantityOnHand=" + quantityOnHand +
                ", quantityReserved=" + quantityReserved +
                ", quantityAvailable=" + quantityAvailable +
                ", reorderLevel=" + reorderLevel +
                ", maxStockLevel=" + maxStockLevel +
                ", location='" + location + '\'' +
                ", lastStockCheck=" + lastStockCheck +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}