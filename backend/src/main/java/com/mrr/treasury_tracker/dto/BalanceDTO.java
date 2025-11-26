package com.mrr.treasury_tracker.dto;

import java.math.BigDecimal;

public class BalanceDTO {
    private BigDecimal amount;

    public BalanceDTO() {
    }

    public BalanceDTO(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
