package com.mrr.treasury_tracker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseDTO {
    private String description;
    private BigDecimal amount;
    private String category;
    private LocalDate date;

    public ExpenseDTO() {
    }
    public ExpenseDTO(String description, BigDecimal amount, String category, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

}
