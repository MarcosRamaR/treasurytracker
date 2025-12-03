package com.mvm.transaction.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data //Manage constructor, getters and setters
@Entity
@Table(name = "balances")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_balance", nullable = false)
    private BigDecimal totalBalance;

    @Column(name= "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String modifiedBy;


    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
