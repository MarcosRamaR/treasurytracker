package com.mvm.transaction.dto;


//DTO to unify expenses and incomes in one to export data

import java.math.BigDecimal;
import java.time.LocalDate;

public class AllTransactionsDTO {
    private Long id;
    private BigDecimal amount;
    private String description;
    private String category;
    private LocalDate date;
    private String type;
    private Long userId;

    public AllTransactionsDTO() {
    }

    public AllTransactionsDTO(Long id, BigDecimal amount, String description, String category, LocalDate date, String type, Long userId) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
        this.type = type;
        this.userId = userId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
