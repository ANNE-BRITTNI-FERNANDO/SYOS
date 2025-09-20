package syos.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * OrderItem model representing individual items within an order
 */
public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Order order;
    private Integer productId;
    private Product product;
    private Integer batchId;
    private Batch batch;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public OrderItem() {}

    public OrderItem(Integer orderId, Integer productId, Integer quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxRate = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        calculateAmounts();
    }

    public OrderItem(Integer id, Integer orderId, Integer productId, Integer batchId,
                    Integer quantity, BigDecimal unitPrice, BigDecimal subtotal,
                    BigDecimal taxRate, BigDecimal taxAmount, BigDecimal discountAmount,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be a positive integer");
        }
        this.orderId = orderId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            this.orderId = order.getId();
        }
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
            this.unitPrice = product.getUnitPrice();
            if (product.isTaxable()) {
                this.taxRate = product.getTaxRate();
            }
            calculateAmounts();
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive integer");
        }
        this.quantity = quantity;
        calculateAmounts();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be null or negative");
        }
        this.unitPrice = unitPrice;
        calculateAmounts();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
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
        calculateAmounts();
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount amount cannot be null or negative");
        }
        this.discountAmount = discountAmount;
        calculateAmounts();
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
    public boolean isValidOrderItem() {
        return orderId != null && orderId > 0 &&
               productId != null && productId > 0 &&
               quantity != null && quantity > 0 &&
               unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) >= 0;
    }

    public void calculateAmounts() {
        if (quantity != null && unitPrice != null) {
            // Calculate subtotal
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            
            // Apply discount if any
            if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                this.subtotal = this.subtotal.subtract(discountAmount);
            }
            
            // Calculate tax amount
            if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
                this.taxAmount = this.subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100));
            } else {
                this.taxAmount = BigDecimal.ZERO;
            }
        } else {
            this.subtotal = BigDecimal.ZERO;
            this.taxAmount = BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalAmount() {
        return subtotal.add(taxAmount);
    }

    public BigDecimal getOriginalSubtotal() {
        if (quantity != null && unitPrice != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    public void applyDiscount(BigDecimal discount) {
        if (discount == null || discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount cannot be null or negative");
        }
        BigDecimal originalSubtotal = getOriginalSubtotal();
        if (discount.compareTo(originalSubtotal) > 0) {
            throw new IllegalArgumentException("Discount cannot be greater than original subtotal");
        }
        this.discountAmount = discount;
        calculateAmounts();
    }

    public void removeDiscount() {
        this.discountAmount = BigDecimal.ZERO;
        calculateAmounts();
    }

    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasTax() {
        return taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasBatch() {
        return batchId != null && batchId > 0;
    }

    public BigDecimal getDiscountPercentage() {
        if (!hasDiscount()) return BigDecimal.ZERO;
        BigDecimal originalSubtotal = getOriginalSubtotal();
        if (originalSubtotal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return discountAmount.multiply(BigDecimal.valueOf(100)).divide(originalSubtotal, 2, java.math.RoundingMode.HALF_UP);
    }

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id) && 
               Objects.equals(orderId, orderItem.orderId) && 
               Objects.equals(productId, orderItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, productId);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", batchId=" + batchId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + subtotal +
                ", taxRate=" + taxRate +
                ", taxAmount=" + taxAmount +
                ", discountAmount=" + discountAmount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}