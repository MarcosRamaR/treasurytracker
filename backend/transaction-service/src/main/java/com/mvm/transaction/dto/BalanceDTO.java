package com.mvm.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDTO {

    private Long id;
    private Long userId;
    private BigDecimal totalBalance;
    private String modifiedBy;
    private LocalDateTime updatedAt;
}