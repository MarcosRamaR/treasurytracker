package com.mvm.transaction.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data //Manage constructor, getters and setters
@Entity
@Table(name = "incomes")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String description;

    private String category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "user_id", nullable = false)
    private Long userId;

}
