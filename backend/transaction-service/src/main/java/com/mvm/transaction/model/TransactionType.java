package com.mvm.transaction.model;

/**
 * Tipo de transacción financiera.
 * Usado como discriminador en herencia JOINED.
 */
public enum TransactionType {
    EXPENSE,
    INCOME
}