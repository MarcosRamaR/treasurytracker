package com.mvm.transaction.exception;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(String message) {
        super(message);
    }

    public TransactionNotFoundException(Long id) {
        super("Transaction not found with id: " + id);
    }

    public TransactionNotFoundException(Long id, Long userId) {
        super("Transaction not found with id: " + id + " for user: " + userId);
    }
}