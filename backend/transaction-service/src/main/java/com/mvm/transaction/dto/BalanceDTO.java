package com.mvm.transaction.dto;

import java.math.BigDecimal;

//Balance will be more attributes, for that BalanceDTO is necessary even if for the moment is the same as balance entity
public class BalanceDTO {
    private Long id;
    private Long userId;
    private BigDecimal totalBalance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }
}
