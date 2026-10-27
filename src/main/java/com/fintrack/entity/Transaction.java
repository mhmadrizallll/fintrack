
package com.fintrack.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "TRANSACTIONS",
    indexes = {
        @Index(name = "IDX_TRANSACTION_DATE", columnList = "TRANSACTION_DATE"),
        @Index(name = "IDX_TRANSACTION_TYPE", columnList = "TYPE")
    }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "TRANSACTION_CODE",
        nullable = false,
        unique = true,
        length = 50
    )
    private String transactionCode;

    @Column(name = "TRANSACTION_DATE", nullable = false)
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 20)
    private TransactionType type;

    // ==========================
    // CATEGORY
    // ==========================

    // ==========================
    // CATEGORY
    // Wajib untuk INCOME dan EXPENSE
    // Opsional untuk TRANSFER
    // ==========================
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "CATEGORY_ID", nullable = true)
    private Category category;

    // ==========================
    // SOURCE FUND
    // ==========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FUND_SOURCE_ID", nullable = false)
    private FundSource fundSource;

    // ==========================
    // DESTINATION FUND
    // Khusus transaksi TRANSFER
    // ==========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DESTINATION_FUND_SOURCE_ID")
    private FundSource destinationFundSource;

    // ==========================
    // AMOUNT
    // ==========================

    @Column(
        name = "AMOUNT",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal amount;

    // ==========================
    // DESCRIPTION
    // ==========================

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    // ==========================
    // ATTACHMENT
    // ==========================

    @Column(name = "ATTACHMENT", length = 500)
    private String attachment;

    // ==========================
    // CREATED BY
    // ==========================

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    // ==========================
    // TIMESTAMP
    // ==========================

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    // ==========================
    // LIFECYCLE
    // ==========================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ==========================
    // GETTER & SETTER
    // ==========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public FundSource getFundSource() {
        return fundSource;
    }

    public void setFundSource(FundSource fundSource) {
        this.fundSource = fundSource;
    }

    public FundSource getDestinationFundSource() {
        return destinationFundSource;
    }

    public void setDestinationFundSource(
        FundSource destinationFundSource
    ) {
        this.destinationFundSource = destinationFundSource;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
