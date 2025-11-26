package com.mrr.treasury_tracker.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="balances")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name= "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String modifiedBy;


    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Balance() {
    }

    public Balance(BigDecimal amount, User user, LocalDateTime updatedAt, String modifiedBy) {
        this.amount = amount;
        this.user = user;
        this.updatedAt = updatedAt;
        this.modifiedBy = modifiedBy;
    }

    public Balance(Long id, BigDecimal amount, User user, LocalDateTime updatedAt, String modifiedBy) {
        this.id = id;
        this.amount = amount;
        this.user = user;
        this.updatedAt = updatedAt;
        this.modifiedBy = modifiedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
