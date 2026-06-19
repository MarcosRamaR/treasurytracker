package com.mvm.transaction.dto;

import com.mvm.transaction.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllTransactionsDTO {

    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private String category;
    private LocalDate date;
    private Long userId;
}