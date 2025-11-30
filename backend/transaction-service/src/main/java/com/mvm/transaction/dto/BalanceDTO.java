package com.mvm.transaction.dto;

//Balance will be more attributes, for that BalanceDTO is necessary even if for the moment is the same as balance entity
public class BalanceDTO {
    private Long id;
    private Long userId;
    private Double totalBalance;

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

    public Double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Double totalBalance) {
        this.totalBalance = totalBalance;
    }
}
