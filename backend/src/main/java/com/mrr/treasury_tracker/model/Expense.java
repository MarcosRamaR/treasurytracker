package com.mrr.treasury_tracker.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="expenses")
@Data //Lombok: Set getters and setters

public class Expense {

    @Id //Set this field as PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;


    public Expense(){
        //Void constructor needed by JPA
    }
    public Expense(Long id, String description, BigDecimal amount, String category, LocalDate date,
                   LocalDateTime createdAt, LocalDateTime updatedAt){
        this.id=id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist //JPA: runs automatically before saving new data
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate //JPA: run automatically before update a data
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    //without id and dates that sets automatically
    public Expense(String description, BigDecimal amount, String category, LocalDate date){
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }
}
