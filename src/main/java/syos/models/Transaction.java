package syos.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transaction model representing financial transactions
 */
public class Transaction {
    private Integer id;
    private String transactionNumber;
    private TransactionType transactionType;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String description;
    private String referenceNumber;
    private Integer orderId;
    private Integer customerId;
    private Integer userId;
    private User user;
    private Customer customer;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enums
    public enum TransactionType {
        SALE, RETURN, REFUND, PAYMENT, EXPENSE, ADJUSTMENT
    }

    public enum PaymentMethod {
        CASH, CREDIT_CARD, DEBIT_CARD, MOBILE_PAYMENT, BANK_TRANSFER, CHECK, OTHER
    }

    // Constructors
    public Transaction() {}

    public Transaction(String transactionNumber, TransactionType transactionType, BigDecimal amount,
                      PaymentMethod paymentMethod, Integer userId) {
        this.transactionNumber = transactionNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.userId = userId;
        this.transactionDate = LocalDateTime.now();
    }

    public Transaction(Integer id, String transactionNumber, TransactionType transactionType,
                      BigDecimal amount, PaymentMethod paymentMethod, String description,
                      String referenceNumber, Integer orderId, Integer customerId, Integer userId,
                      LocalDateTime transactionDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.transactionNumber = transactionNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.referenceNumber = referenceNumber;
        this.orderId = orderId;
        this.customerId = customerId;
        this.userId = userId;
        this.transactionDate = transactionDate;
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

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        if (transactionNumber == null || transactionNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction number cannot be null or empty");
        }
        this.transactionNumber = transactionNumber.trim().toUpperCase();
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        if (userId == null || userId <= 0) {
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        }
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
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
    public boolean isValidTransaction() {
        return transactionNumber != null && !transactionNumber.trim().isEmpty() &&
               transactionType != null &&
               amount != null &&
               paymentMethod != null &&
               userId != null && userId > 0 &&
               transactionDate != null;
    }

    public boolean isPositiveAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegativeAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZeroAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isSaleTransaction() {
        return transactionType == TransactionType.SALE;
    }

    public boolean isReturnTransaction() {
        return transactionType == TransactionType.RETURN;
    }

    public boolean isRefundTransaction() {
        return transactionType == TransactionType.REFUND;
    }

    public boolean isPaymentTransaction() {
        return transactionType == TransactionType.PAYMENT;
    }

    public boolean isExpenseTransaction() {
        return transactionType == TransactionType.EXPENSE;
    }

    public boolean isCashTransaction() {
        return paymentMethod == PaymentMethod.CASH;
    }

    public boolean isCardTransaction() {
        return paymentMethod == PaymentMethod.CREDIT_CARD || 
               paymentMethod == PaymentMethod.DEBIT_CARD;
    }

    public boolean isElectronicTransaction() {
        return paymentMethod == PaymentMethod.MOBILE_PAYMENT || 
               paymentMethod == PaymentMethod.BANK_TRANSFER;
    }

    public boolean hasCustomer() {
        return customerId != null && customerId > 0;
    }

    public boolean hasOrder() {
        return orderId != null && orderId > 0;
    }

    public BigDecimal getAbsoluteAmount() {
        return amount.abs();
    }

    public String getFormattedAmount() {
        return String.format("%.2f", amount);
    }

    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && Objects.equals(transactionNumber, that.transactionNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionNumber);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionNumber='" + transactionNumber + '\'' +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", description='" + description + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", orderId=" + orderId +
                ", customerId=" + customerId +
                ", userId=" + userId +
                ", transactionDate=" + transactionDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}