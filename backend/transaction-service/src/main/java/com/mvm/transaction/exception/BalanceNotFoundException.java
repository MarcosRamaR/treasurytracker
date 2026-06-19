package com.mvm.transaction.exception;

public class BalanceNotFoundException extends RuntimeException {

    public BalanceNotFoundException(String message) {
        super(message);
    }

    public BalanceNotFoundException(Long userId) {
        super("Balance not found for user: " + userId);
    }
}