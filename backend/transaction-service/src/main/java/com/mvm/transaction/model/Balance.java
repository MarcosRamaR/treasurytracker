package com.mvm.transaction.model;

import jakarta.persistence.*;
import lombok.Data;

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
    private Double totalBalance;
}
